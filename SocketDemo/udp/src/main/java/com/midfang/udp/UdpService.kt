package com.midfang.udp

import java.net.DatagramPacket
import java.net.DatagramSocket

/**
 *     author : midFang
 *     time   : 2021/05/03
 *     desc   :
 *     version: 1.0
 */
fun main() {
    val datagramSocket = DatagramSocket(13405)

    val byteArray = ByteArray(1024)
    val datagramPacket = DatagramPacket(byteArray, byteArray.size)

    println("udp 启动中")
    // 接受 DatagramPacket 消息
    datagramSocket.receive(datagramPacket)

    println("接收到的数据： " + datagramPacket.data.toString(Charsets.UTF_8))

}