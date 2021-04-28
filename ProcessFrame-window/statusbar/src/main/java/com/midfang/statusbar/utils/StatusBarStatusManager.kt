package com.midfang.statusbar.utils

import android.os.RemoteCallbackList
import android.os.RemoteException
import com.midfang.statusbar.IStatusBarCallback

/**
 *     author : midFang
 *     time   : 2021/04/16
 *     desc   : 状态管理监听器
 *     version: 1.0
 */
object StatusBarStatusManager {

    /**
     *  RemoteCallbackList 保证在多进程中解注册正常
     */
    private val remoteCallbackList by lazy { RemoteCallbackList<IStatusBarCallback>() }

    fun addStatusBarListener(callback: IStatusBarCallback?) {
        remoteCallbackList.register(callback)
    }

    fun removeStatusBarListener(callback: IStatusBarCallback?) {
        remoteCallbackList.unregister(callback)
    }

    fun onClosedCallback() {
        try {
            remoteCallbackList.let { callback ->
                // 遍历的时候,需要配合使用
                val size = callback.beginBroadcast()
                repeat(size) {
                    val broadcastItem = callback.getBroadcastItem(it)
                    // closed 方法也运行在 binder 线程池中,避免 onClosedCallback 方法在主线程调用导致 ANR
                    broadcastItem.closed()
                }
                callback.finishBroadcast()
            }
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    fun onOpenedCallback() {
        try {
            remoteCallbackList.let { callback ->
                // 遍历的时候,需要配合使用
                val size = callback.beginBroadcast()
                repeat(size) {
                    val broadcastItem = callback.getBroadcastItem(it)
                    broadcastItem.opened()
                }
                callback.finishBroadcast()
            }
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }
}