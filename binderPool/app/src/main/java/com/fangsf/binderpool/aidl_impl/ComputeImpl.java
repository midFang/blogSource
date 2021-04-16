package com.fangsf.binderpool.aidl_impl;

import android.os.RemoteException;
import android.util.Log;

import com.fangsf.binderpool.ICompute;

/**
 * Created by fangsf on 2019-05-23
 * Useful:
 */
public class ComputeImpl extends ICompute.Stub {

    private static final String TAG = "ComputeImpl";

    @Override
    public int add(int a, int b) throws RemoteException {

        Log.e(TAG, "add: " + " thread name " + Thread.currentThread().getName());

        return a + b;
    }
}
