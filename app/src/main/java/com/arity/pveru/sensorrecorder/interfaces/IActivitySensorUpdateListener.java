package com.arity.pveru.sensorrecorder.interfaces;

import android.location.Location;

import com.google.android.gms.location.ActivityRecognitionResult;

/**
 * Created by pveru on 2/3/18.
 */

public interface IActivitySensorUpdateListener {

    void onActivityUpdate(ActivityRecognitionResult activityRecognitionResult);

}
