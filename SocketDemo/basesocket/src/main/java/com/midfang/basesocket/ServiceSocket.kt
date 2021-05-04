package com.midfang.basesocket

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket

/**
 *     author : midFang
 *     time   : 2021/05/03
 *     desc   :
 *     version: 1.0
 */

fun main() {

    // 指定端口号
    val serviceSocket = ServerSocket(12377)

    // 会发生阻塞
    println("开始连接...")
    val socket = serviceSocket.accept()
    println("已连接--${socket.localAddress.hostAddress}:${socket.localPort}")

    // 获取客户端发送过来的消息
    val inputStream = socket.getInputStream()

    val inn = InputStreamReader(inputStream)
    val bufferedReader = BufferedReader(inn)
    var data = ""
    while (bufferedReader.readLine()?.also { data = it } != null) {
        println("客户端发送过来的信息: $data")
    }

    println("关闭连接")
    socket.shutdownInput()
    socket.close()
}