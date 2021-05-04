// IMessageCallbacl.aidl
package com.midfang.socketdemo;

// Declare any non-default types here with import statements

interface IMessageCallback {
    void receiverMessage(String message);
}