package com.midfang.processbinderwindow

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.ThreadUtils
import com.midfang.statusbar.StatusBarViewManager
import com.midfang.statusbar.impl.StatusBarCallbackImpl
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    private val mStatusBarManager by lazy { StatusBarViewManager.instance }

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ScreenUtils.setFullScreen(this)
        ScreenUtils.setLandscape(this)

        findViewById<View>(R.id.button)
            .setOnClickListener {
                mStatusBarManager.statusBarManager?.open()
            }

        findViewById<View>(R.id.button2)
            .setOnClickListener {
                mStatusBarManager.statusBarManager?.close()
            }

        findViewById<View>(R.id.button4).setOnClickListener {
            mStatusBarManager.statusBarStatus?.isDropDown?.let { isDropDown ->
                mStatusBarManager.statusBarManager?.let {
                    it.setEnableDropDown(!isDropDown)
                }
            }
        }

        findViewById<View>(R.id.button3)
            .setOnClickListener {
                mStatusBarManager.statusBarStatus?.let {
                    Log.d(TAG, "onCreate() called  it.isDropDown ${it.isDropDown}  it.isWholeOpened  ${it.isWholeOpened}  isThread ${ThreadUtils.isMainThread()}")
                }
            }


        PermissionUtils.requestDrawOverlays(object : PermissionUtils.SimpleCallback {
            override fun onGranted() {
                // 申请成功, 打开悬浮窗
                thread {
                    mStatusBarManager.init(this@MainActivity)


                    mStatusBarManager.statusBarManager?.addStatusBarListener(
                        statusBarListener
                    )

                    mStatusBarManager.statusBarStatus
                }
            }

            override fun onDenied() {
                Toast.makeText(this@MainActivity, "granted", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private val statusBarListener = object : StatusBarCallbackImpl() {
        override fun opened() {
            // 运行在客户端的 binder 线程池中
            Log.d(TAG, "opened() called ${ThreadUtils.isMainThread()}")
        }

        override fun closed() {
            Log.d(TAG, "closed() called ${ThreadUtils.isMainThread()}")
        }
    }

    override fun onDestroy() {
        StatusBarViewManager.instance.statusBarManager?.removeStatusBarListener(statusBarListener)
        super.onDestroy()
    }


}