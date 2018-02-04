package com.arity.pveru.sensorrecorder.interfaces;

import android.location.Location;

/**
 * Created by pveru on 2/3/18.
 */

public interface ILocationSensorUpdateListeners {
    
    void onLocationUpdate(Location location);
}
