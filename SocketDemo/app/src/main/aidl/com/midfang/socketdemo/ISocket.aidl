// ISocket.aidl
package com.midfang.socketdemo;

import com.midfang.socketdemo.IMessageCallback;

interface ISocket {

    boolean sendMessage(String message);

    void addMessageCallback(in IMessageCallback back);

    void removeMessageCallback(in IMessageCallback back);


}