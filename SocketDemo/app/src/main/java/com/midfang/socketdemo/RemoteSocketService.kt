package com.midfang.socketdemo

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.RemoteCallbackList
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.lang.ref.WeakReference
import java.net.Socket
import kotlin.concurrent.thread

/**
 * dec: 维护长连接的服务
 *
 * 1. 该 service 需要开启多进程, 需要考虑进程报活
 * 2. 需要考虑 NAT 超时情况, 需要维护长连接
 * 3. 可以理解该服务就是连接远程服务端 socket 并和远程服务端进行消息通信
 *
 * @author midFang
 *
 */
class RemoteSocketService : Service() {

    private val remoteCallbackList by lazy { RemoteCallbackList<IMessageCallback>() }

    private var sendTime = 0L
    private var mSocket: WeakReference<Socket>? = null
    private var mReaderThread: ReaderThread? = null


    private val mHandler = Handler(Looper.getMainLooper())

    /**心跳任务，不断重复调用自己*/
    private val heartBeatRunnable = object : Runnable {
        override fun run() {
            if (System.currentTimeMillis() - sendTime >= HEART_BEAT_RATE) {
                val isSuccess = sendMsg("HeartBeat") //就发送一个\r\n过去 如果发送失败，就重新初始化一个socket
                if (!isSuccess) {
                    mHandler.removeCallbacks(this)
                    mReaderThread?.release()
                    releaseLastSocket(mSocket)
                    InitSocketThread().start()
                }
            }
            mHandler.postDelayed(this, HEART_BEAT_RATE)
        }
    }

    companion object {
        /**
         * 可设置比 NAT 超时的时间少, 一般各大运营商的时间都各不相同
         * 长链接的核心本质就是避免 NAT 超时时间
         * 因为即使 TCP 的时间并没有中断, 但是还是会因为通信双方并发发生任何数据交互,从而运行商会主动关闭连接,这个就是 NAT 超时
         */
        private const val HEART_BEAT_RATE = 3 * 1000.toLong()

        // 本机电脑的 ip
        private var HOST: String = "192.168.1.172"
        private var PORT: Int = 12377

        /**
         * 连接服务
         */
        fun connect(context: Context, serviceConnection: ServiceConnection) {
            Intent(context.applicationContext, RemoteSocketService::class.java).also {
                context.applicationContext.bindService(
                    it,
                    serviceConnection,
                    Context.BIND_AUTO_CREATE
                )
            }
        }
    }


    /**
     * 客户端调用服务端的方法.
     */
    private val mRemoteService = object : ISocket.Stub() {
        /**
         * @throws RemoteSocketService
         */
        override fun sendMessage(message: String?) = sendMsg(message)
        override fun addMessageCallback(back: IMessageCallback?) {
            remoteCallbackList.register(back)
        }

        override fun removeMessageCallback(back: IMessageCallback?) {
            remoteCallbackList.unregister(back)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = mRemoteService

    /**
     * 大致的步骤有:
     * 1. 连接 socket 服务
     * 2. 解析数据
     * 3. 返回给外部数据
     */

    override fun onCreate() {
        super.onCreate()
        // 1. 开始连接 socket 服务
        InitSocketThread().start()
    }

    inner class InitSocketThread : Thread() {
        override fun run() {
            super.run()
            initSocket()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacks(heartBeatRunnable)
        mReaderThread?.release()
        releaseLastSocket(mSocket)
    }

    private fun initSocket() {
        println("端口$HOST:$PORT")
        val socket = Socket(HOST, PORT)
        // 2 小时内发送一次, 并无法保证 NAT 超时
//        socket.keepAlive = true

        mSocket = WeakReference(socket)
        // 开启读写线程
        mReaderThread = ReaderThread(socket)
        mReaderThread?.start()

        mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE)
    }

    inner class ReaderThread(socket: Socket) : Thread() {

        private var isStart = true
        private val mWeakSocket by lazy { WeakReference<Socket>(socket) }

        fun release() {
            isStart = false
            releaseLastSocket(mWeakSocket)
        }

        override fun run() {
            super.run()

            kotlin.runCatching {
                mWeakSocket.get()?.let { socket ->
                    val inputStream = socket.getInputStream()
                    val byteArray = ByteArray(1024 * 4)
                    var length = 0

                    while (!socket.isClosed && !socket.isInputShutdown && isStart
                        && inputStream.read(byteArray).also { length = it } != -1
                    ) {
                        if (length > 0) {
                            // 解析数据
                            val message = String(byteArray.copyOf(length))
                            println("接收到的消息 $message")

                            // 回复数据
                            val size = remoteCallbackList.beginBroadcast()
                            repeat(size) {
                                val item = remoteCallbackList.getBroadcastItem(it)
                                item.receiverMessage(message)
                            }
                            remoteCallbackList.finishBroadcast()
                        }
                    }
                }
            }

        }
    }

    private fun releaseLastSocket(weakSocket: WeakReference<Socket>?) {
        kotlin.runCatching {
            var get = weakSocket?.get()
            if (get?.isClosed == true) {
                get?.close()
            }
            get = null
        }
    }

    /**
     * 发送消息
     */
    private fun sendMsg(message: String?): Boolean {
        if (mSocket == null || mSocket?.get() == null) return false

        mSocket?.get()?.let { socket ->

            if (!socket.isClosed && !socket.isOutputShutdown) {
                thread {
                    val outputStream = socket.getOutputStream()
                    val message: String = message + "\r\n"
                    kotlin.runCatching {
                        outputStream.write(message.toByteArray())
                        outputStream.flush()
                    }
                }

                sendTime = System.currentTimeMillis() // 每次发送成数据，就改一下最后成功发送的时间，节省心跳间隔时间

            } else return false
        }

        return true
    }
}

