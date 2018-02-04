package com.arity.pveru.sensorrecorder.interfaces;

import android.hardware.SensorEvent;
import android.location.Location;

/**
 * Created by pveru on 2/3/18.
 */

public interface ISensorUpdateListeners {

    void onSensorUpdate(SensorEvent sensorEvent);
}
