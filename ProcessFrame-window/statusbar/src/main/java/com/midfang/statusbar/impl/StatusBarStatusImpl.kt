package com.midfang.statusbar.impl

import com.midfang.statusbar.IStatusBarStatus
import com.midfang.statusbar.StatusBarViewManager

/**
 *     author : midFang
 *     time   : 2021/04/15
 *     desc   : 状态
 *     version: 1.0
 */
class StatusBarStatusImpl : IStatusBarStatus.Stub() {

    override fun isDropDown() = StatusBarViewManager.instance.statusBarView?.isDropDown() ?: false


    override fun isWholeOpened()  = StatusBarViewManager.instance.statusBarView?.isWholeOpened() ?: false

}