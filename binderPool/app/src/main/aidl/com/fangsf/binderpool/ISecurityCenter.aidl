// ISecurityCenter.aidl
package com.fangsf.binderpool;


// 加密中心的aidl
interface ISecurityCenter {

    //加密
    String encrypt(String content);
    //解密
    String decrypt(String password);


}
