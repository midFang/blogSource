// IStatusBarManager.aidl
package com.midfang.statusbar;

// Declare any non-default types here with import statements
import com.midfang.statusbar.IStatusBarCallback;



interface IStatusBarManager {
     void open();

     void close();

     void setEnableDropDown(boolean isDropDown);

     void addStatusBarListener(in IStatusBarCallback callback);

     void removeStatusBarListener(in IStatusBarCallback callback);
}