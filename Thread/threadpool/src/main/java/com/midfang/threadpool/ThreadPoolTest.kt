package com.midfang.threadpool

import java.util.concurrent.*

class ThreadPoolTest {

}

fun main() {
    // SynchronousQueue
    // ArrayBlockingQueue
    // LinkedBlockingDeque
    val threadPoolExecutor = ThreadPoolExecutor(
        1, Int.MAX_VALUE,
        60, TimeUnit.SECONDS,
        SynchronousQueue<Runnable>()
    )
    threadPoolExecutor.execute {
        println("任务一")
        while (true){}
    }
    threadPoolExecutor.execute {
        println("任务二")
    }

    threadPoolExecutor.execute {
        println("任务三")
    }
}