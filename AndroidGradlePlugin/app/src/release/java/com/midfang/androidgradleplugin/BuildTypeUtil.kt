package com.midfang.androidgradleplugin

import android.app.Activity
import android.graphics.Color
import android.view.View
import android.view.ViewGroup

/**
 *     author : midFang
 *     time   : 2021/04/12
 *     desc   :
 *     version: 1.0
 */
object BuildTypeUtil {

    fun drawBadge(activity: Activity) {
        val decorView: ViewGroup = activity.window.decorView as ViewGroup
        val view = View(activity)
        view.setBackgroundColor(Color.RED)
        decorView.addView(view, 200, 200)
    }
    

}