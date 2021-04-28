package com.midfang.baseprocesslib

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.midfang.baseprocesslib.location.ILocationManager
import com.midfang.ipc.IPC
import com.midfang.ipc.IPCService

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        startService(Intent(this, GpsService::class.java))


        // 创建多进程连接 -- 建立和服务端 GpsService 同样的进程服务,以便与进程间通信
        IPC.connect(this, IPCService.Companion.IPCService0::class.java)
    }

    fun viewclick(view: View) {

        val location = IPC.getInstanceWithName(
                IPCService.Companion.IPCService0::class.java,
                ILocationManager::class.java,
                "getDefault"
        )

        Toast.makeText(this, "${location?.location?.toString()}", Toast.LENGTH_SHORT).show()

    }

    fun viewclick2(view: View) {

        val location = IPC.getInstanceWithName(
                IPCService.Companion.IPCService0::class.java,
                ILocationManager::class.java,
                "getDefault"
        )

        Toast.makeText(this, "${location?.getLocationByLat(33.1).toString()}", Toast.LENGTH_SHORT).show()
    }
}