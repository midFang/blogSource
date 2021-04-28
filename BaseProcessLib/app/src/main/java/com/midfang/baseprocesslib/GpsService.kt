package com.midfang.baseprocesslib

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.midfang.baseprocesslib.location.Location
import com.midfang.baseprocesslib.location.LocationManager
import com.midfang.ipc.IPC

/**
 *     author : midFang
 *     time   : 2021/04/25
 *     desc   :
 *     version: 1.0
 */
class GpsService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        // 在 gps 进程中
        // 建立该进程 需要提供的服务信息

        LocationManager.getDefault().location = Location("深圳市区", 1.1, 2.2)


        // 注册服务: 在该进程中组装相关参数, 以便于向外部提供调用
        // 其实后续的客户端的调用就是通过传递方法的参数, 在服务端中进行反射执行调用
        IPC.register(LocationManager::class.java)


    }
}