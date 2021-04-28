package com.midfang.ipc

import android.content.Context
import com.midfang.ipc.model.Request
import java.lang.reflect.Proxy

/**
 *     author : midFang
 *     time   : 2021/04/27
 *     desc   :
 *     version: 1.0
 */
object IPC {


    fun register(service: Class<*>) {
        Registry.register(service)
    }

    fun connect(context: Context, service: Class<out IPCService>) {
        Channel.bind(context, null, service)
    }

    fun <T> getInstance(
            service: Class<out IPCService>,
            classType: Class<T>,
            vararg parameters: Any
    ): T? {
        return getInstanceWithName(
                service, classType, "getInstance",
                *parameters
        )
    }

    fun <T> getInstanceWithName(
            service: Class<out IPCService>,
            classType: Class<T>,
            methodName: String,
            vararg parameters: Any?
    ): T? {
        if (!classType.isInterface) throw RuntimeException("getInstanceWithName classType must interface ")

        val serviceId = classType.getAnnotation(ServiceId::class.java)

        // 获取单例对象
        val response = Channel.send(Request.GET_INSTANCE, service, serviceId?.value, methodName, parameters.requireNoNulls())

        response?.let {
            if (response.isSuccess) {
                return Proxy.newProxyInstance(
                        classType.classLoader, arrayOf<Class<*>>(classType),
                        IPCInvocationHandler(service, serviceId?.value)
                ) as T
            }
        }

        return null
    }


}