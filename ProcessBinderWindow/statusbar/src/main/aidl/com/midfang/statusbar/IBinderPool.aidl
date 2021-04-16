// IBinderPool.aidl
package com.midfang.statusbar;

// Declare any non-default types here with import statements

interface IBinderPool {

     // 连接池, 根据对应的 code 返回具体的 binder 对象
     IBinder queryBinder( int binderCode);
}