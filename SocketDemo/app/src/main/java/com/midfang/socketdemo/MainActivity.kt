package com.midfang.socketdemo

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class MainActivity : AppCompatActivity() {

    private val stringBuilder by lazy { StringBuilder() }

    private var iSocket: ISocket? = null


    private val messageCallbackImpl = object : MessageCallbackImpl() {
        override fun receiverMessage(message: String?) {
            when (message) {
                "ok"->{
                    stringBuilder.append("心跳机制触发").appendln()
                    updateUI()
                }
                else ->{
                    stringBuilder.append("接受到 ServerSocket 发送过来的消息: $message").appendln()
                    updateUI()
                }
            }
        }
    }

    private fun updateUI() {
        runOnUiThread {
            findViewById<TextView>(R.id.tv_mes).text = stringBuilder.toString()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.btn_send).setOnClickListener {
            val message = findViewById<EditText>(R.id.et_mes).text.toString()
            iSocket?.sendMessage(message)
            findViewById<TextView>(R.id.tv_mes).text = "soctet 发送消息: ${stringBuilder.append(message).appendln().toString()}"
            findViewById<TextView>(R.id.tv_mes).text = ""
        }


        // 连接 socket 端口
        RemoteSocketService.connect(this, serviceConnection)
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            iSocket?.removeMessageCallback(messageCallbackImpl)
            iSocket = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            iSocket = ISocket.Stub.asInterface(service)
            iSocket?.addMessageCallback(messageCallbackImpl)
        }
    }


}