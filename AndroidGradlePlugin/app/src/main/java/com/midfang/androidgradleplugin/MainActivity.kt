package com.midfang.androidgradleplugin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        BuildTypeUtil.drawBadge(this)


        // url 可以这样简化
        println(BuildConfig.SERVER_URL)
    }
}