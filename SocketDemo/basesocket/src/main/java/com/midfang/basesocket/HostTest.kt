package com.midfang.basesocket

import java.net.InetAddress

/**
 *     author : midFang
 *     time   : 2021/05/04
 *     desc   :
 *     version: 1.0
 */
fun main() {
    val address = InetAddress.getLocalHost()
    println("计算机: " + address.hostName)
    println("IP地址: " + address.hostAddress)
}