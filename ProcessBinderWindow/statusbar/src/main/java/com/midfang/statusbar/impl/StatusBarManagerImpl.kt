package com.midfang.statusbar.impl

import android.nfc.tech.IsoDep
import android.util.Log
import com.midfang.statusbar.IStatusBarCallback
import com.midfang.statusbar.IStatusBarManager
import com.midfang.statusbar.StatusBarViewManager
import com.midfang.statusbar.utils.StatusBarStatusManager
import com.midfang.statusbar.utils.isMainThread
import com.midfang.statusbar.utils.runOnUiThread

/**
 *     author : midFang
 *     time   : 2021/04/15
 *     desc   : 运行在 Binder 线程池中
 *     version: 1.0
 */
class StatusBarManagerImpl : IStatusBarManager.Stub() {
    companion object {
        private const val TAG = "IStatusBarManagerImpl"
    }

    override fun setEnableDropDown(isDropDown: Boolean) {
        runOnUiThread {
            StatusBarViewManager.instance.statusBarView?.let {
                if (isDropDown) {
                    it.enableStatusBar()
                } else {
                    it.disableStatusBar()
                }
            }
        }
    }

    override fun open() {
        runOnUiThread {
            StatusBarViewManager.instance.statusBarView?.expand()
        }
    }

    override fun addStatusBarListener(callback: IStatusBarCallback?) {
        Log.d(TAG, "addStatusBarListener() isMainThread ${isMainThread()}")
        StatusBarStatusManager.addStatusBarListener(callback)
    }

    override fun removeStatusBarListener(callback: IStatusBarCallback?) {
        StatusBarStatusManager.removeStatusBarListener(callback)
    }

    override fun close() {
        runOnUiThread {
            StatusBarViewManager.instance.statusBarView?.close()
        }
    }
}