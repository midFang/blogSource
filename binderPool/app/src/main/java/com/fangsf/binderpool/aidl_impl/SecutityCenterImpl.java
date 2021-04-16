package com.fangsf.binderpool.aidl_impl;

import android.os.RemoteException;
import android.util.Log;

import com.fangsf.binderpool.ISecurityCenter;

/**
 * Created by fangsf on 2019-05-23
 * Useful: aidl  ISecurityCenter  实现类
 */
public class SecutityCenterImpl extends ISecurityCenter.Stub {

    private static final char SECRET_CODE = '^';

    private static final String TAG = "SecutityCenterImpl";

    @Override
    public String encrypt(String content) throws RemoteException {
        Log.e(TAG, "encrypt: " + " thread name "+ Thread.currentThread().getName());

        // 加密
        char[] chars = content.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            chars[i] ^= SECRET_CODE;
        }

        return new String(chars);
    }

    @Override
    public String decrypt(String password) throws RemoteException {
        Log.e(TAG, "decrypt: " +" thread name "+Thread.currentThread().getName());

        return encrypt(password);
    }
}
