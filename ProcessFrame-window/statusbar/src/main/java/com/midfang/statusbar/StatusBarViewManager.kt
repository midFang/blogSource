package com.midfang.statusbar

import android.content.Context
import androidx.annotation.WorkerThread
import com.midfang.statusbar.pool.BinderPool

/**
 * author : midFang
 * time   : 2021/04/15
 * desc   :
 * version: 1.0
 */
class StatusBarViewManager {

    internal var statusBarView: StatusBarView? = null

    var statusBarManager: IStatusBarManager? = null

    var statusBarStatus: IStatusBarStatus? = null

    @WorkerThread
    fun init(context: Context) {
        val binderPool = BinderPool.getInstance(context.applicationContext)

        val queryBinder = binderPool.queryBinder(BinderPool.BINDER_MANAGER)

        statusBarManager = IStatusBarManager.Stub.asInterface(queryBinder)

        statusBarStatus =
            IStatusBarStatus.Stub.asInterface(binderPool.queryBinder(BinderPool.BINDER_STATUS))
    }


    companion object {
        val instance: StatusBarViewManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            StatusBarViewManager()
        }
    }
}