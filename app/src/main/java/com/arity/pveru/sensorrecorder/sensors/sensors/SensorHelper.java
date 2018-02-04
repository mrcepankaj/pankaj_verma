package com.arity.pveru.sensorrecorder.sensors.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.arity.pveru.sensorrecorder.common.Utils;
import com.arity.pveru.sensorrecorder.interfaces.ISensorUpdateListeners;

/**
 * Created by pveru on 2/3/18.
 */

public class SensorHelper implements SensorEventListener {

    private final SensorManager mSensorManager;

    private final Sensor mSensor1;
    private final Sensor mSensor2;
    private final Sensor mSensor3;
    private final Sensor mSensor4;
    private final Sensor mSensor5;
    private final Sensor mSensor6;
    private final Sensor mSensor7;
    private final Sensor mSensor8;
    private final Sensor mSensor9;
    private final Sensor mSensor10;
    private final Sensor mSensor11;

    static ISensorUpdateListeners iSensorUpdateListener;

    public static void setUpdateListener(ISensorUpdateListeners listener) {
        iSensorUpdateListener = listener;
    }

    public SensorHelper(Context context) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        mSensor1 = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensor2 = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mSensor3 = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensor4 = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mSensor5 = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensor6 = mSensorManager.getDefaultSensor(Sensor.TYPE_MOTION_DETECT);
        mSensor7 = mSensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION);
        mSensor8 = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mSensor9 = mSensorManager.getDefaultSensor(Sensor.TYPE_STATIONARY_DETECT);
        mSensor10 = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mSensor11 = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
    }

    public void startSensorRecording() {
        mSensorManager.registerListener(this, mSensor1, 1000000);
        mSensorManager.registerListener(this, mSensor2, 1000000);
        mSensorManager.registerListener(this, mSensor3, 1000000);
        mSensorManager.registerListener(this, mSensor4, 1000000);
        mSensorManager.registerListener(this, mSensor5, 1000000);
        mSensorManager.registerListener(this, mSensor6, 1000000);
        mSensorManager.registerListener(this, mSensor7, 1000000);
        mSensorManager.registerListener(this, mSensor8, 1000000);
        mSensorManager.registerListener(this, mSensor9, 1000000);
        mSensorManager.registerListener(this, mSensor10, 1000000);
        mSensorManager.registerListener(this, mSensor11, 1000000);
    }

    public void stopSensorRecording() {
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (iSensorUpdateListener != null)
            iSensorUpdateListener.onSensorUpdate(event);

        Utils.createFileWithHeader(event.sensor.getType());
        Utils.addSensorData(event.sensor.getType(), Utils.getSensorTypeString(event.sensor.getType()), event.timestamp, event.accuracy, event.values);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
