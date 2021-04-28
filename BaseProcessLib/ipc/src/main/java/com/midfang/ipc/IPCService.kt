package com.midfang.ipc

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.google.gson.Gson
import com.midfang.ipc.model.Parameters
import com.midfang.ipc.model.Request
import com.midfang.ipc.model.Response
import java.lang.reflect.Method

/**
 *     author : midFang
 *     time   : 2021/04/27
 *     desc   :
 *     version: 1.0
 */
abstract class IPCService : Service() {

    companion object {
        class IPCService0 : IPCService()
        class IPCService1 : IPCService()
        class IPCService2 : IPCService()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return IPCServiceBinder()
    }

}

