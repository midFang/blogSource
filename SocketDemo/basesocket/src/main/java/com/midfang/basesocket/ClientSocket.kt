package com.midfang.basesocket

import java.io.PrintWriter
import java.net.Socket

/**
 *     author : midFang
 *     time   : 2021/05/03
 *     desc   :
 *     version: 1.0
 */
fun main() {
    // 连接服务端
    val clientSocket = Socket("127.0.0.1", 12377)

    // 获取输出流, 和服务端进行通信
    val outputStream = clientSocket.getOutputStream()
    // 将输出流包装成打印流
    val printWriter = PrintWriter(outputStream)
    printWriter.write("hello world")
    printWriter.flush()
    clientSocket.shutdownOutput() //关闭输出流
    clientSocket.close()
}