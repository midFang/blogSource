package com.midfang.baseprocesslib.location;



import com.midfang.ipc.ServiceId;


/**
 * 提供地理位置信息
 * 该类可实现, 或不实现 ILocationManager
 */
@ServiceId("LocationManager")
public class LocationManager implements ILocationManager {

    private static final LocationManager ourInstance = new LocationManager();

    public static LocationManager getDefault() {
        return ourInstance;
    }

    private LocationManager() {
    }

    private Location location;

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocationByLat(double lat){
        return location;
    }

    public Location getLocation() {
        return location;
    }

}

