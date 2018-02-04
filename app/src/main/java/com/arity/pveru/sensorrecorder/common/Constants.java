/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.arity.pveru.sensorrecorder.common;

import android.os.Environment;

import com.google.android.gms.location.DetectedActivity;

import java.io.File;

/**
 * Constants used in this sample.
 */
public final class Constants {

    private Constants() {
    }

    public static String BASE_PATH;
    /**
     * Data Format to be used in trip data upload
     */
    public static final String DATE_FORMAT_YYYY_MM_DD_T_HH_MM_SSZ = "yyyy-MM-dd'T'HH:mm:ssZ";

    public static final String PACKAGE_NAME =
            "com.arity.pveru.sensorrecorder";

    public static final String KEY_ACTIVITY_UPDATES_REQUESTED = PACKAGE_NAME +
            ".ACTIVITY_UPDATES_REQUESTED";

    //public static final String KEY_DETECTED_ACTIVITIES = PACKAGE_NAME + ".DETECTED_ACTIVITIES";
    //public static final String KEY_DETECTED_LOCATIONS = PACKAGE_NAME + ".DETECTED_LOCATIONS";

    public static final int ACTIVITY_RECOGNITION_REQUEST_CODE = 9999;
    public static final String ACTION_ACTIVITY_DETECTED = "com.arity.pveru.sensorrecorder.sensors.activity.ACTIVITY_DETECTED";

    /**
     * The desired time between activity detections. Larger values result in fewer activity
     * detections while improving battery life. A value of 0 results in activity detections at the
     * fastest possible rate.
     */
    public static final long DETECTION_INTERVAL_IN_MILLISECONDS = 1 * 1000; // 30 seconds

    /**
     * Activity recognition restart interval if the activity recognition fails.
     */
    public static final long ACTIVITY_RECOGNITION_RESTART_INTERVAL = 2 * 60 * 1000;

    /**
     * List of DetectedActivity types that we monitor in this sample.
     */
    public static final int[] MONITORED_ACTIVITIES = {
            DetectedActivity.STILL,
            DetectedActivity.ON_FOOT,
            DetectedActivity.WALKING,
            DetectedActivity.RUNNING,
            DetectedActivity.ON_BICYCLE,
            DetectedActivity.IN_VEHICLE,
            DetectedActivity.TILTING,
            DetectedActivity.UNKNOWN
    };

    public static class FileNames {

        public static final String APPINFO = "SensorRecorderAppInfo.txt";
        public static final String LOGS = "SensorRecorderLogs.txt";
        public static final String LOCATION = "SensorRecorderLocationData.txt";
        public static final String ACTIVITY = "SensorRecorderActivityData.txt";
        public static final String SENSOR = "SensorRecorderSensor_";

    }

    public static class FileHeaders {

        public static final String APPINFO = "appVersion,phoneInfo,osVersion";
        public static final String SENSOR = "type,name,time,accuracy,v1,v2,v3,v4,v5,v6,v7";
        public static final String ACTIVITY = "timestamp,IN_VEHICLE,ON_BICYCLE,ON_FOOT,STILL,UNKNOWN,TILTING,WALKING,RUNNING";
        public static final String LOCATION = "timestamp,altitude,course,horizontalAccuracy,latitude,longitude,rawSpeed";

    }

    public static class FilePath {
        public static final String LOGS = BASE_PATH + FileNames.LOGS;
        public static final String ACTIVITY = BASE_PATH + FileNames.ACTIVITY;
        public static final String LOCATION = BASE_PATH + FileNames.LOCATION;
        public static final String SENSOR = BASE_PATH + FileNames.SENSOR;
        public static final String APPINFO = BASE_PATH + FileNames.APPINFO;
    }
}
