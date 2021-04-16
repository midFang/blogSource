package com.midfang.statusbar.impl

import com.midfang.statusbar.IStatusBarCallback

/**
 *     author : midFang
 *     time   : 2021/04/16
 *     desc   : 内部实现, 外部直接调用, 运行在 binder 线程池中
 *     version: 1.0
 */
abstract class StatusBarCallbackImpl : IStatusBarCallback.Stub() {
    abstract override fun opened()

    abstract override fun closed()
}