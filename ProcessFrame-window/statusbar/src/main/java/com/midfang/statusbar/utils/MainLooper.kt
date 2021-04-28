package com.midfang.statusbar.utils

import android.os.Handler
import android.os.Looper


inline fun runOnUiThread(crossinline runnable: () -> Unit) {
    MainLooper.runOnUiThread(Runnable {
        runnable.invoke()
    })
}

inline fun isMainThread() = MainLooper.isMainThread()

open class MainLooper protected constructor(looper: Looper) : Handler(looper) {
    companion object {
        private val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            MainLooper(Looper.getMainLooper())
        }

        fun isMainThread() = Looper.getMainLooper() == Looper.myLooper()

        fun runOnUiThread(runnable: Runnable) {
            if (isMainThread()) {
                runnable.run()
            } else {
                instance.post(runnable)
            }
        }
    }
}