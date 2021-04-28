// IStatusBarStatus.aidl
package com.midfang.statusbar;

// Declare any non-default types here with import statements

interface IStatusBarStatus {

 // 是否可以下拉
 boolean isDropDown();

 // 是否完全展开
 boolean isWholeOpened();

}