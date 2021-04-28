package com.midfang.ipc

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import android.text.TextUtils
import com.google.gson.Gson
import com.midfang.ipc.model.Parameters
import com.midfang.ipc.model.Request
import com.midfang.ipc.model.Response
import java.util.concurrent.ConcurrentHashMap

/**
 *     author : midFang
 *     time   : 2021/04/27
 *     desc   :
 *     version: 1.0
 */
object Channel {
    /**
     * 存储给客户端调用的实例 binder 对象
     */
    private val mBinders by lazy {
        ConcurrentHashMap<Class<out IPCService>, IIPCService>()
    }

    private val mGson by lazy { Gson() }


    fun bind(context: Context, packageName: String?, service: Class<out IPCService>) {
        val intent = if (!TextUtils.isEmpty(packageName)) {
            //跨app的绑定
            Intent().apply { setClassName(packageName!!, service.name) }
        } else {
            Intent(context, service)
        }

        context.bindService(intent, IPCServiceConnection(service), Context.BIND_AUTO_CREATE)
    }

    fun send(
            type: Int,
            service: Class<out IPCService>,
            serviceId: String?,
            methodName: String?,
            parameters: Array<out Any>?
    ): Response? {

        // 封装一个请求, 让服务端执行
        val request = Request(type, serviceId, methodName, makeParameters(parameters))

        val binder = mBinders[service]
        return try {
            binder?.send(request)
        } catch (e: RemoteException) {
            e.printStackTrace()
            //也可以把null变成错误信息
            Response(null, false)
        }
    }

    /**
     *
     */
    private fun makeParameters(objects: Array<out Any>?): Array<Parameters?>? {
        val parameters: Array<Parameters?>
        if (objects != null) {
            parameters = arrayOfNulls(objects.size)
            for (i in objects.indices) {
                parameters[i] = Parameters(objects[i].javaClass.name, mGson.toJson(objects[i]))
            }
        } else {
            parameters = arrayOfNulls(0)
        }
        return parameters
    }

    class IPCServiceConnection(private val mService: Class<out IPCService?>) : ServiceConnection {

        override fun onServiceDisconnected(name: ComponentName?) {
            mBinders.remove(mService)
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mBinders.put(mService, IIPCService.Stub.asInterface(service))
        }
    }


}