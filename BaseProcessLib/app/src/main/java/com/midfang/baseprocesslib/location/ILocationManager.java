package com.midfang.baseprocesslib.location;


import com.midfang.ipc.ServiceId;

/**
 * 提供地理位置的规则
 */
@ServiceId("LocationManager")
public interface ILocationManager {

     Location getLocation();


     Location getLocationByLat(double lat);
}

