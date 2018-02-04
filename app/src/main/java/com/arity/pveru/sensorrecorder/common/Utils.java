package com.arity.pveru.sensorrecorder.common;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.location.Location;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.arity.pveru.sensorrecorder.R;
import com.google.android.gms.location.DetectedActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility methods used in this sample.
 */
public class Utils {

    private Utils() {
    }

    /**
     * Metho to get time in W3C format passed on the given pattern
     *
     * @param timeInMillis time in millis
     * @param pattern      pattern for returning the time format
     * @return time in W3C format
     */
    public static String getTimeW3C(long timeInMillis,
                                    String pattern) {
        String str = "---";
        SimpleDateFormat dateFormat = new SimpleDateFormat();
        try {
            dateFormat.applyPattern(pattern);
            str = dateFormat.format(new Date(timeInMillis));
            str = str.substring(0, str.length() - 2) + ":"
                    + str.substring(str.length() - 2);
            //str = str.replace("T", " ");
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e("TAG", ex.getLocalizedMessage());
        }
        return str;
    }

    /**
     * Method to return the Application name.
     *
     * @param context - Context passed to get the PackageManager Instance.
     * @return - returns Application name
     */
    public static String getApplicationName(Context context) {

        ApplicationInfo applicationInfo = null;
        PackageManager packageManager = null;
        if (context != null) {
            packageManager = context.getPackageManager();

            try {
                applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            } catch (final PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        return (String) ((applicationInfo != null && packageManager != null) ? packageManager.getApplicationLabel(applicationInfo) : "");
    }

    /**
     * Util function to convert meter per second to miles per hour.
     *
     * @param meterPerSecond meter per second value.
     * @return miles per hour value.
     */
    public static float convertMpsToMph(double meterPerSecond) {
        return (float) (2.23694 * meterPerSecond);
    }

    public static void putLog(String log) {
        FileManager.getInstance(Constants.FilePath.LOGS, ExecutorHelper.getLogExecutorInstance()).writeData(Utils.getTimeW3C(Calendar.getInstance().getTimeInMillis(), Constants.DATE_FORMAT_YYYY_MM_DD_T_HH_MM_SSZ) + ": " + log + "\n", true);
    }

    public static void putErrorLog(String errorLog) {
        FileManager.getInstance(Constants.FilePath.LOGS, ExecutorHelper.getLogExecutorInstance()).writeData(Utils.getTimeW3C(Calendar.getInstance().getTimeInMillis(), Constants.DATE_FORMAT_YYYY_MM_DD_T_HH_MM_SSZ) + ": " + "Exception: " + errorLog + "\n", true);
    }

    public static void addActivityData(long timeStamp, String activityDataString) {
        FileManager.getInstance(Constants.FilePath.ACTIVITY, ExecutorHelper.getActivityExecutorInstance()).writeData(Utils.getTimeW3C(timeStamp, Constants.DATE_FORMAT_YYYY_MM_DD_T_HH_MM_SSZ) + "," + activityDataString + "\n", true);
    }

    public static void addLocationData(long timeStamp, String locationDataString) {
        FileManager.getInstance(Constants.FilePath.LOCATION, ExecutorHelper.getLocationExecutorInstance()).writeData(Utils.getTimeW3C(timeStamp, Constants.DATE_FORMAT_YYYY_MM_DD_T_HH_MM_SSZ) + "," + locationDataString + "\n", true);
    }

    /**
     * Returns a human readable String corresponding to a detected activity type.
     */
    public static String getActivityString(Context context, int detectedActivityType) {
        Resources resources = context.getResources();
        switch (detectedActivityType) {
            case DetectedActivity.IN_VEHICLE:
                return resources.getString(R.string.in_vehicle);
            case DetectedActivity.ON_BICYCLE:
                return resources.getString(R.string.on_bicycle);
            case DetectedActivity.ON_FOOT:
                return resources.getString(R.string.on_foot);
            case DetectedActivity.RUNNING:
                return resources.getString(R.string.running);
            case DetectedActivity.STILL:
                return resources.getString(R.string.still);
            case DetectedActivity.TILTING:
                return resources.getString(R.string.tilting);
            case DetectedActivity.UNKNOWN:
                return resources.getString(R.string.unknown);
            case DetectedActivity.WALKING:
                return resources.getString(R.string.walking);
            default:
                return resources.getString(R.string.unidentifiable_activity, detectedActivityType);
        }
    }

    public static void putActivityToFile(long timeStamp, ArrayList<DetectedActivity> detectedActivities) {


        HashMap<Integer, Integer> detectedActivitiesMap = new HashMap<>();
        detectedActivitiesMap.put(DetectedActivity.IN_VEHICLE, 0);
        detectedActivitiesMap.put(DetectedActivity.ON_BICYCLE, 0);
        detectedActivitiesMap.put(DetectedActivity.ON_FOOT, 0);
        detectedActivitiesMap.put(DetectedActivity.RUNNING, 0);
        detectedActivitiesMap.put(DetectedActivity.STILL, 0);
        detectedActivitiesMap.put(DetectedActivity.UNKNOWN, 0);
        detectedActivitiesMap.put(DetectedActivity.WALKING, 0);
        detectedActivitiesMap.put(DetectedActivity.TILTING, 0);
        detectedActivitiesMap.put(-1, 0);

        for (DetectedActivity da : detectedActivities) {

            switch (da.getType()) {
                case DetectedActivity.IN_VEHICLE:
                    detectedActivitiesMap.put(DetectedActivity.IN_VEHICLE, da.getConfidence());
                    break;
                case DetectedActivity.ON_BICYCLE:
                    detectedActivitiesMap.put(DetectedActivity.ON_BICYCLE, da.getConfidence());
                    break;
                case DetectedActivity.ON_FOOT:
                    detectedActivitiesMap.put(DetectedActivity.ON_FOOT, da.getConfidence());
                    break;
                case DetectedActivity.RUNNING:
                    detectedActivitiesMap.put(DetectedActivity.RUNNING, da.getConfidence());
                    break;
                case DetectedActivity.STILL:
                    detectedActivitiesMap.put(DetectedActivity.STILL, da.getConfidence());
                    break;
                case DetectedActivity.TILTING:
                    detectedActivitiesMap.put(DetectedActivity.TILTING, da.getConfidence());
                    break;
                case DetectedActivity.UNKNOWN:
                    detectedActivitiesMap.put(DetectedActivity.UNKNOWN, da.getConfidence());
                    break;
                case DetectedActivity.WALKING:
                    detectedActivitiesMap.put(DetectedActivity.WALKING, da.getConfidence());
                    break;
            }
        }

        StringBuilder activityString = new StringBuilder();
        activityString.append(detectedActivitiesMap.get(DetectedActivity.IN_VEHICLE)).append(",")
                .append(detectedActivitiesMap.get(DetectedActivity.ON_BICYCLE)).append(",")
                .append(detectedActivitiesMap.get(DetectedActivity.ON_FOOT)).append(",")
                .append(detectedActivitiesMap.get(DetectedActivity.STILL)).append(",")
                .append(detectedActivitiesMap.get(DetectedActivity.UNKNOWN)).append(",")
                .append(detectedActivitiesMap.get(DetectedActivity.TILTING)).append(",")
                .append(detectedActivitiesMap.get(DetectedActivity.WALKING)).append(",")
                .append(detectedActivitiesMap.get(DetectedActivity.RUNNING));


        addActivityData(timeStamp, activityString.toString());

    }

    public static void putLocationToFile(long timeStamp, Location location) {

        //To increase the performance of repeated string concatenation.Using StringBuffer.append() instead of string + operator.
        StringBuilder value = new StringBuilder(getTimeW3C(location.getTime(), Constants.DATE_FORMAT_YYYY_MM_DD_T_HH_MM_SSZ));
        value.append(",");
        value.append(location.getAltitude());
        value.append(",");
        value.append(location.getBearing());
        value.append(",");
        value.append(location.getAccuracy());
        value.append(",");
        value.append(location.getLatitude());
        value.append(",");
        value.append(location.getLongitude());
        value.append(",");
        value.append(convertMpsToMph(location.getSpeed()));
        value.append(",");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            value.append(location.getVerticalAccuracyMeters());
        } else {
            value.append(" ");
        }

        value.append("\n");
        value.toString();

        addLocationData(timeStamp, value.toString());
    }

    public static void showToast(Context context, String message) {

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static String getSensorTypeString(int value) {
        switch (value) {
            case Sensor.TYPE_ACCELEROMETER:
                return "TYPE_ACCELEROMETER";

            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                return "TYPE_AMBIENT_TEMPERATURE";

            case Sensor.TYPE_GAME_ROTATION_VECTOR:
                return "TYPE_GAME_ROTATION_VECTOR";

            case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
                return "TYPE_GEOMAGNETIC_ROTATION_VECTOR";

//            case Sensor.TYPE_GLANCE_GESTURE:
//                return "TYPE_GLANCE_GESTURE";

            case Sensor.TYPE_GRAVITY:
                return "TYPE_GRAVITY";

            case Sensor.TYPE_GYROSCOPE:
                return "TYPE_GYROSCOPE";

            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                return "TYPE_GYROSCOPE_UNCALIBRATED";

            case Sensor.TYPE_HEART_RATE:
                return "TYPE_HEART_RATE";

            case Sensor.TYPE_LIGHT:
                return "TYPE_LIGHT";

            case Sensor.TYPE_LINEAR_ACCELERATION:
                return "TYPE_LINEAR_ACCELERATION";

            case Sensor.TYPE_MAGNETIC_FIELD:
                return "TYPE_MAGNETIC_FIELD";

            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                return "TYPE_MAGNETIC_FIELD_UNCALIBRATED";

//            case Sensor.TYPE_PICK_UP_GESTURE:
//                return "TYPE_PICK_UP_GESTURE";

            case Sensor.TYPE_PRESSURE:
                return "TYPE_PRESSURE";

            case Sensor.TYPE_PROXIMITY:
                return "TYPE_PROXIMITY";

            case Sensor.TYPE_RELATIVE_HUMIDITY:
                return "TYPE_RELATIVE_HUMIDITY";

            case Sensor.TYPE_ROTATION_VECTOR:
                return "TYPE_ROTATION_VECTOR";

            case Sensor.TYPE_SIGNIFICANT_MOTION:
                return "TYPE_SIGNIFICANT_MOTION";

            case Sensor.TYPE_STEP_COUNTER:
                return "TYPE_STEP_COUNTER";

            case Sensor.TYPE_STEP_DETECTOR:
                return "TYPE_STEP_DETECTOR";

//            case Sensor.TYPE_TILT_DETECTOR:
//                return "SENSOR_STRING_TYPE_TILT_DETECTOR";
//
//            case Sensor.TYPE_WAKE_GESTURE:
//                return "TYPE_WAKE_GESTURE";

            case Sensor.TYPE_ORIENTATION:
                return "TYPE_ORIENTATION";

            case Sensor.TYPE_TEMPERATURE:
                return "TYPE_TEMPERATURE";

//            case Sensor.TYPE_DEVICE_ORIENTATION:
//                return "TYPE_DEVICE_ORIENTATION";
//
//            case Sensor.TYPE_DYNAMIC_SENSOR_META:
//                return "TYPE_DYNAMIC_SENSOR_META";

            case Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT:
                return "TYPE_LOW_LATENCY_OFFBODY_DETECT";

            case Sensor.TYPE_ACCELEROMETER_UNCALIBRATED:
                return "TYPE_ACCELEROMETER_UNCALIBRATED";

            default:
                return "UNKNOWN";
        }
    }


    public static void addSensorData(int sensorType, String sensorTypeName, long timeStamp, int accuracy, float[] values) {

        StringBuilder builder = new StringBuilder(sensorType + "," + sensorTypeName + ","
                + Utils.getTimeW3C(timeStamp, Constants.DATE_FORMAT_YYYY_MM_DD_T_HH_MM_SSZ) + ","
                + accuracy);
        for (int i = 0; i < values.length; i++) {
            builder.append(",");
            builder.append(values[i]);
        }

        FileManager.getInstance(Constants.FilePath.SENSOR + sensorTypeName + ".csv",
                ExecutorHelper.getMEMSExecutorInstance()).writeData(builder.toString()
                + "\n", true);
    }

    public static void createFileWithHeader(int sensorType) {

        String name = Constants.FilePath.SENSOR + getSensorTypeString(sensorType) + ".csv";

        File file = new File(name);
        if (!file.exists())
            FileManager.getInstance(name,
                    ExecutorHelper.getMEMSExecutorInstance()).writeData(Constants.FileHeaders.SENSOR + "\n", false);

    }
}