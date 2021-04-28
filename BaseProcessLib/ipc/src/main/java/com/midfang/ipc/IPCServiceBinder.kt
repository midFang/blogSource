package com.midfang.ipc

import com.google.gson.Gson
import com.midfang.ipc.model.Parameters
import com.midfang.ipc.model.Request
import com.midfang.ipc.model.Response

/**
 *     author : midFang
 *     time   : 2021/04/27
 *     desc   :
 *     version: 1.0
 */
class IPCServiceBinder : IIPCService.Stub() {

    private val mGson by lazy { Gson() }

    override fun send(request: Request): Response? {
        // 根据客户端的请求,执行具体的方法
        val methodName = request.methodName
        val parameters = request.parameters
        val serviceId = request.serviceId
        val parametersObj = getParameters(parameters)

        val method = Registry.findMethod(serviceId, methodName, parametersObj)

        when (request.type) {
            // 执行单例中的实例化方法
            Request.GET_INSTANCE -> {
                return try {
                    // 从 Request 中找出客户端传递过来的具体哪个方法去执行
                    method?.isAccessible = true
                    println("结果 ${parametersObj.size}")
                    val instance = method?.invoke(null, *parametersObj)
                    Registry.putObjInstance(serviceId, instance)
                    Response(null, true)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Response(null, false)
                }
            }

            // 执行指定的方法
            Request.GET_METHOD -> {
                return try {
                    val objInstance = Registry.getObjInstance(serviceId)
                    method?.isAccessible = true
                    val result = method?.invoke(objInstance, *parametersObj)
                    Response(mGson.toJson(result), true)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Response(null, false)
                }
            }
        }

        return null
    }

    private fun getParameters(parameters: Array<Parameters>): Array<Any> {
        val objects = arrayOfNulls<Any>(parameters.size)
        for (i in parameters.indices) {
            val parameter = parameters[i]
            //还原
            try {
                objects[i] = mGson.fromJson(parameter.value, Class.forName(parameter.type))
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
        }

        return objects.requireNoNulls()
    }

}
