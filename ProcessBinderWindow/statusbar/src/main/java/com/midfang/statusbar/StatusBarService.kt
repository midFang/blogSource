package com.midfang.statusbar;

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.midfang.statusbar.pool.BinderPool

/**
 * author : midFang
 * time   : 2021/04/15
 * desc   :
 * version: 1.0
 */
class StatusBarService : Service() {

    override fun onBind(intent: Intent): IBinder {
        return BinderPool.BinderPoolImpl()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        val statusBarView = StatusBarView(this)
        StatusBarViewManager.instance.statusBarView = statusBarView
        statusBarView.addToWindow()

    }
}
