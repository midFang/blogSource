package com.midfang.ipc

import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

/**
 *     author : midFang
 *     time   : 2021/04/27
 *     desc   : 负责记录 服务端注册的信息
 *     version: 1.0
 */
object Registry {

    private const val LEFT_BRACKET = "("
    private const val RIGHT_BRACKET = ")"
    private const val COMMA = ","

    /**
     * 单例对象的实例
     */
    private val mObjInstance by lazy {
        ConcurrentHashMap<String, Any?>()
    }

    /**
     * 服务表
     */
    private val mServices by lazy {
        ConcurrentHashMap<String, Class<*>>()
    }

    /**
     * 方法表
     */
    private val mMethods by lazy {
        ConcurrentHashMap<Class<*>, ConcurrentHashMap<String, Method>>()
    }


    /**
     * 服务端注册
     */
    fun register(service: Class<*>) {
        val serviceId = service.getAnnotation(ServiceId::class.java)
                ?: throw RuntimeException("ServiceId must be register")

        // 获取 javaClz 类上所有类信息(方法,属性,返回值等待)
        val serviceIdValue = serviceId.value
        mServices[serviceIdValue] = service

        // 存储方法表
        val serviceMethods = service.methods

        // key 为方法参数, value 为具体的 method 对象
        val methods = mMethods.getOrPut(service, { ConcurrentHashMap() })

        // 组装方法参数, 拼接后的参数: getName(String s1, String s2)
        mMethods[service] = methods
        serviceMethods.forEach {
            val stringBuilder = StringBuilder(it.name)
            stringBuilder.append(LEFT_BRACKET)

            // 因为有重载方法的存在，所有不能以方法名作为 key, 带上参数作为key
            val typeParameters = it.parameterTypes
            if (typeParameters.isNotEmpty()) {
                stringBuilder.append(getParameterTypeNameCompat(typeParameters[0]))
            }

            for (index in 1 until typeParameters.size) {
                stringBuilder.append(COMMA).append(getParameterTypeNameCompat(typeParameters[index]))
            }

            stringBuilder.append(RIGHT_BRACKET)
            methods[stringBuilder.toString()] = it
        }


        val entries: Set<Map.Entry<String, Class<*>>> = mServices.entries
        for ((key, value) in entries) {
            println("服务表:$key = $value")
        }

        val entrySet: Set<Map.Entry<Class<*>, ConcurrentHashMap<String, Method>>> = mMethods.entries
        for ((key, value1) in entrySet) {
            println("方法表：$key")
            for ((key1) in value1) {
                println(" $key1")
            }
        }

    }

    /**
     * @return 返回类的名称
     * 兼容原始类型, 方式 java 自动类型包装
     * 比如 double 和 Double 是不一样的
     */
    private fun getParameterTypeNameCompat(clazz: Class<*>): String {
        return if (clazz.isPrimitive) {
            clazz.kotlin.javaObjectType.name
        } else {
            clazz.name
        }
    }

    fun findMethod(serviceId: String, methodName: String, parametersObj: Array<Any>): Method? {
        val service = mServices[serviceId]
        val methods = mMethods[service]

        return methods?.get(getMethodParams(methodName, parametersObj))
    }

    /**
     * 获取方法的拼接参数: getName(String s1, String s2)
     */
    private fun getMethodParams(methodName: String, parametersObj: Array<Any>): String {
        val stringBuilder = StringBuilder(methodName)
        stringBuilder.append(LEFT_BRACKET)

        if (parametersObj.isNotEmpty()) {
            stringBuilder.append(getParameterTypeNameCompat(parametersObj[0].javaClass))
        }

        for (index in 1 until parametersObj.size) {
            stringBuilder.append(COMMA).append(getParameterTypeNameCompat(parametersObj[index].javaClass))
        }

        stringBuilder.append(RIGHT_BRACKET)
        return stringBuilder.toString()
    }

    fun putObjInstance(serviceId: String, instance: Any?) {
        mObjInstance[serviceId] = instance
    }

    fun getObjInstance(serviceId: String) = mObjInstance[serviceId]

}