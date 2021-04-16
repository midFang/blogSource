package com.fangsf.binderpool;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.fangsf.binderpool.aidl_impl.ComputeImpl;
import com.fangsf.binderpool.aidl_impl.SecutityCenterImpl;

import java.util.concurrent.CountDownLatch;

/**
 * Created by fangsf on 2019-05-23
 * Useful:  binder 连接池管理类
 */
public class BinderPool {
    private static final String TAG = "BinderPool";

    public static final int BINDER_NONE = -1;
    public static final int BINDER_COMPUTE = 0;
    public static final int BINDER_SECURITY_CENTER = 1;

    private Context mContext;
    private IBinderPool mBinderPool;
    private CountDownLatch mConnectBinderPoolCountDownLatch;

    private static volatile BinderPool sInstance;

    private BinderPool(Context context) {
        this.mContext = context;
        connectBinderPoolService();
    }

    public static BinderPool getInstance(Context context) {
        if (sInstance == null) {
            synchronized (BinderPool.class) {
                if (sInstance == null) {
                    sInstance = new BinderPool(context);
                }
            }
        }
        return sInstance;
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinderPool = IBinderPool.Stub.asInterface(service);

            // 设置死亡代理， 断线重连
            try {
                mBinderPool.asBinder().linkToDeath(mBindPoolDeathRecipient, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            mConnectBinderPoolCountDownLatch.countDown();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private IBinder.DeathRecipient mBindPoolDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            //重连，先去除之前的连接
            mBinderPool.asBinder().unlinkToDeath(mBindPoolDeathRecipient, 0);

            mBinderPool = null;

            connectBinderPoolService(); //重连

        }
    };

    private synchronized void connectBinderPoolService() {
        mConnectBinderPoolCountDownLatch = new CountDownLatch(1);
        Intent intent = new Intent(mContext, BinderPoolService.class);
        mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);

        try {
            mConnectBinderPoolCountDownLatch.await(); // 本来是异步的操作，变成同步
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public IBinder queryBinder(int binderCode) {
        IBinder binder = null;

        try {
            // 连接成功后，初始化的值 mBinderPool
            if (mBinderPool != null) {
                // 返回具体的binder
                binder = mBinderPool.queryBinder(binderCode);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return binder;
    }

    public static class BinderPoolImpl extends IBinderPool.Stub {

        public BinderPoolImpl() {
            super();
        }

        @Override
        public IBinder queryBinder(int binderCode) throws RemoteException {

            IBinder iBinder = null;

            // 根据code，返回具体的binder对象
            switch (binderCode) {
                case BINDER_SECURITY_CENTER:
                    iBinder = new SecutityCenterImpl();
                    break;

                case BINDER_COMPUTE:
                    iBinder = new ComputeImpl();
                    break;

                default:
                    break;
            }


            return iBinder;
        }
    }
}
