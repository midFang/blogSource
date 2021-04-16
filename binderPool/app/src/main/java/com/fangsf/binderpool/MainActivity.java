package com.fangsf.binderpool;

import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.fangsf.binderpool.aidl_impl.ComputeImpl;
import com.fangsf.binderpool.aidl_impl.SecutityCenterImpl;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void click1(View view) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                BinderPool binderPool = BinderPool.getInstance(MainActivity.this);

                IBinder securityBinder = binderPool.queryBinder(BinderPool.BINDER_SECURITY_CENTER);

                // 获取客户端可以调用的aidl对象
                ISecurityCenter securityCenter = ISecurityCenter.Stub.asInterface(securityBinder);
                String msg = "helloworld_安卓";
                Log.e(TAG, "msg: " + msg);

                try {
                    String passworld = securityCenter.encrypt(msg);
                    Log.e(TAG, "encrypt: " + passworld);
                    Log.e(TAG, "decrypt: " + securityCenter.decrypt(passworld));

                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                IBinder computeBinder = binderPool.queryBinder(BinderPool.BINDER_COMPUTE);
                ICompute compute = ICompute.Stub.asInterface(computeBinder);

                try {
                    int sub = compute.add(1, 1);
                    Log.e(TAG, "compute.add: " + sub);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    public void button(View view) {
        view.scrollBy(0, -100);

    }
}
