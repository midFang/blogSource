package com.midfang.ipc

import com.google.gson.Gson
import com.midfang.ipc.model.Request
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

/**
 *     author : midFang
 *     time   : 2021/04/27
 *     desc   :
 *     version: 1.0
 */
class IPCInvocationHandler(
    private val service: Class<out IPCService>,
    private val serviceId: String?
) : InvocationHandler {

    val gson by lazy {  Gson() }


    override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any? {

        // 执行接口中的具体方法

        /**
         * 向服务器发起执行 method 的请求
         */
        val response = Channel.send(Request.GET_METHOD, service, serviceId, method?.name, args) ?: return null

        if (response.isSuccess) {
            //方法返回值
            val returnType = method!!.returnType
            if (returnType != Void.TYPE && returnType != Void::class.java) {
                //方法执行后的返回值， json数据
                val source = response.source
                return gson.fromJson(source, returnType)
            }
        }


        return null
    }

}