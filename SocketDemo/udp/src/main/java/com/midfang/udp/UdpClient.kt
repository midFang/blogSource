package com.midfang.udp

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

/**
 *     author : midFang
 *     time   : 2021/05/03
 *     desc   :
 *     version: 1.0
 */

fun main() {

    val msg = "hello world udp"
    val datagramSocket = DatagramSocket()

    val packet = DatagramPacket(msg.toByteArray(), msg.length, InetAddress.getLocalHost(), 13405)

    // 发送 DatagramPacket 消息
    datagramSocket.send(packet)

}