package com.fangsf.binderpool;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by fangsf on 2019-05-23
 * Useful: Binder连接池，service组件
 */
public class BinderPoolService extends Service {

    private static final String TAG = "BinderPoolService";
    private Binder mBinderPool = new BinderPool.BinderPoolImpl();

    @Override
    public void onCreate() {
        super.onCreate();

        Log.e(TAG, "onCreate: " + " thread name " + Thread.currentThread().getName());

    }

    @Override
    public IBinder onBind(Intent intent) {

        Log.e(TAG, "onBind: " + " thread name " + Thread.currentThread().getName());

        return mBinderPool;
    }
}
