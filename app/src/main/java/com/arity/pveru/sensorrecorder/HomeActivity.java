package com.arity.pveru.sensorrecorder;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.arity.pveru.sensorrecorder.adaptors.DetectedSensorsAdapter;
import com.arity.pveru.sensorrecorder.interfaces.IActivitySensorUpdateListener;
import com.arity.pveru.sensorrecorder.common.Utils;
import com.arity.pveru.sensorrecorder.interfaces.ILocationSensorUpdateListeners;
import com.arity.pveru.sensorrecorder.interfaces.ISensorUpdateListeners;
import com.arity.pveru.sensorrecorder.sensors.activity.ActivityDetectionHelper;
import com.arity.pveru.sensorrecorder.adaptors.DetectedActivitiesAdapter;
import com.arity.pveru.sensorrecorder.adaptors.DetectedLocationsAdapter;
import com.arity.pveru.sensorrecorder.sensors.location.GpsLocationProvider;
import com.arity.pveru.sensorrecorder.sensors.sensors.SensorHelper;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements ILocationSensorUpdateListeners, IActivitySensorUpdateListener, ISensorUpdateListeners, View.OnClickListener {

    protected static final String TAG = "MainActivity";

    // Used in checking for runtime permissions.
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    private Context mContext;
    Button btn_StopRecording;

    ListView detectedActivitiesListView;
    ListView detectedLocationsListView;

    ListView detected_sensors_accelerometer_listview;
    ListView detected_sensors_linearAcc_listview;

    ListView detected_sensors_stepCounter_listview;
    ListView detected_sensors_stepDetector_listview;

    ListView detected_sensors_gyro_listview;
    ListView detected_sensors_gravity_listview;

    ListView detected_sensors_motionDetect_listview;
    ListView detected_sensors_significantMotion_listview;

    ListView detected_sensors_proximity_listview;
    ListView detected_sensors_stationaryDetect_listview;


    /**
     * Adapter backed by a list of DetectedActivity objects.
     */
    private DetectedActivitiesAdapter activitiesAdapter;
    private DetectedLocationsAdapter locationsAdapter;

    private DetectedSensorsAdapter sensorAccelerometerAdapter;
    private DetectedSensorsAdapter sensorLinearAccDetectorAdapter;

    private DetectedSensorsAdapter sensorStepCounterAdapter;
    private DetectedSensorsAdapter sensorStepDetectorAdapter;

    private DetectedSensorsAdapter sensorGyroDetectorAdapter;
    private DetectedSensorsAdapter sensorGravityDetectorAdapter;

    private DetectedSensorsAdapter sensorMotionDetectDetectorAdapter;
    private DetectedSensorsAdapter sensorSignificantMotionDetectorAdapter;

    private DetectedSensorsAdapter sensorProximityDetectorAdapter;
    private DetectedSensorsAdapter sensorStationaryDetectDetectorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        mContext = this;

        initUI();

        if (checkPermissions()) {
            if (Utils.isGpsEnabled(this))
                startRecorderService();
            else {
                Utils.showToast(this, "Enable location services and launch the app again!");
                finish();
            }
        } else {
            requestPermissions();
        }
    }


    private void initUI() {

        btn_StopRecording = (Button) findViewById(R.id.btn_StopRecording);
        btn_StopRecording.setVisibility(Button.GONE);
        btn_StopRecording.setOnClickListener(this);

        detectedActivitiesListView = (ListView) findViewById(
                R.id.detected_activities_listview);

        detectedLocationsListView = (ListView) findViewById(
                R.id.detected_locations_listview);


        detected_sensors_accelerometer_listview = (ListView) findViewById(
                R.id.detected_sensors_accelerometer_listview);
        detected_sensors_linearAcc_listview = (ListView) findViewById(
                R.id.detected_sensors_linearAcc_listview);

        detected_sensors_stepCounter_listview = (ListView) findViewById(
                R.id.detected_sensors_stepCounter_listview);
        detected_sensors_stepDetector_listview = (ListView) findViewById(
                R.id.detected_sensors_stepDetector_listview);

        detected_sensors_gyro_listview = (ListView) findViewById(
                R.id.detected_sensors_gyro_listview);
        detected_sensors_gravity_listview = (ListView) findViewById(
                R.id.detected_sensors_gravity_listview);

        detected_sensors_motionDetect_listview = (ListView) findViewById(
                R.id.detected_sensors_motionDetect_listview);
        detected_sensors_significantMotion_listview = (ListView) findViewById(
                R.id.detected_sensors_significantMotion_listview);

        detected_sensors_stationaryDetect_listview = (ListView) findViewById(
                R.id.detected_sensors_stationaryDetect_listview);
        detected_sensors_proximity_listview = (ListView) findViewById(
                R.id.detected_sensors_proximity_listview);


        // Bind the adapter to the ListView responsible for display data for detected activities.
        activitiesAdapter = new DetectedActivitiesAdapter(this, new ArrayList<DetectedActivity>());
        detectedActivitiesListView.setAdapter(activitiesAdapter);

        // Bind the adapter to the ListView responsible for display data for detected locations.
        locationsAdapter = new DetectedLocationsAdapter(this, new ArrayList<Location>());
        detectedLocationsListView.setAdapter(locationsAdapter);

        sensorAccelerometerAdapter = new DetectedSensorsAdapter(this, new ArrayList<SensorEvent>());
        detected_sensors_accelerometer_listview.setAdapter(sensorAccelerometerAdapter);

        sensorLinearAccDetectorAdapter = new DetectedSensorsAdapter(this, new ArrayList<SensorEvent>());
        detected_sensors_linearAcc_listview.setAdapter(sensorLinearAccDetectorAdapter);

        sensorStepCounterAdapter = new DetectedSensorsAdapter(this, new ArrayList<SensorEvent>());
        detected_sensors_stepCounter_listview.setAdapter(sensorStepCounterAdapter);

        sensorStepDetectorAdapter = new DetectedSensorsAdapter(this, new ArrayList<SensorEvent>());
        detected_sensors_stepDetector_listview.setAdapter(sensorStepDetectorAdapter);

        sensorGyroDetectorAdapter = new DetectedSensorsAdapter(this, new ArrayList<SensorEvent>());
        detected_sensors_gyro_listview.setAdapter(sensorGyroDetectorAdapter);

        sensorGravityDetectorAdapter = new DetectedSensorsAdapter(this, new ArrayList<SensorEvent>());
        detected_sensors_gravity_listview.setAdapter(sensorGravityDetectorAdapter);

        sensorMotionDetectDetectorAdapter = new DetectedSensorsAdapter(this, new ArrayList<SensorEvent>());
        detected_sensors_motionDetect_listview.setAdapter(sensorMotionDetectDetectorAdapter);

        sensorSignificantMotionDetectorAdapter = new DetectedSensorsAdapter(this, new ArrayList<SensorEvent>());
        detected_sensors_significantMotion_listview.setAdapter(sensorSignificantMotionDetectorAdapter);

        sensorProximityDetectorAdapter = new DetectedSensorsAdapter(this, new ArrayList<SensorEvent>());
        detected_sensors_proximity_listview.setAdapter(sensorProximityDetectorAdapter);

        sensorStationaryDetectDetectorAdapter = new DetectedSensorsAdapter(this, new ArrayList<SensorEvent>());
        detected_sensors_stationaryDetect_listview.setAdapter(sensorStationaryDetectDetectorAdapter);

    }

    private void startRecorderService() {

        //Starting the service
        Intent intent = new Intent(mContext, SensorRecorderService.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mContext.getApplicationContext().startForegroundService(intent);
        } else {
            mContext.getApplicationContext().startService(intent);
        }

        btn_StopRecording.setVisibility(Button.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        GpsLocationProvider.setUpdateListener(this);
        ActivityDetectionHelper.setUpdateListener(this);
        SensorHelper.setUpdateListener(this);
    }

    @Override
    protected void onPause() {

        GpsLocationProvider.setUpdateListener(null);
        ActivityDetectionHelper.setUpdateListener(null);
        SensorHelper.setUpdateListener(null);
        super.onPause();
    }


    ////////////////////////////PERMISSIONS///////////////////////////

    /**
     * Returns the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void requestPermissions() {

        ActivityCompat.requestPermissions(HomeActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
                Utils.showToast(HomeActivity.this, "Launch again and give the permission");
                finish();
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
                startRecorderService();
            } else {
                Utils.showToast(HomeActivity.this, "Launch again and give the permission");
                finish();
            }
        }
    }

    @Override
    public void onLocationUpdate(Location location) {
        locationsAdapter.updateLocations(location);
    }

    @Override
    public void onActivityUpdate(ActivityRecognitionResult activityRecognitionResult) {
        activitiesAdapter.updateActivities(activityRecognitionResult.getProbableActivities());
    }

    @Override
    public void onSensorUpdate(SensorEvent sensorEvent) {

        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                sensorAccelerometerAdapter.updateSensors(sensorEvent);
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                sensorLinearAccDetectorAdapter.updateSensors(sensorEvent);
                break;
            case Sensor.TYPE_STEP_COUNTER:
                sensorStepCounterAdapter.updateSensors(sensorEvent);
                break;
            case Sensor.TYPE_STEP_DETECTOR:
                sensorStepDetectorAdapter.updateSensors(sensorEvent);
                break;
            case Sensor.TYPE_GYROSCOPE:
                sensorGyroDetectorAdapter.updateSensors(sensorEvent);
                break;
            case Sensor.TYPE_GRAVITY:
                sensorGravityDetectorAdapter.updateSensors(sensorEvent);
                break;
            case Sensor.TYPE_MOTION_DETECT:
                sensorMotionDetectDetectorAdapter.updateSensors(sensorEvent);
                break;
            case Sensor.TYPE_SIGNIFICANT_MOTION:
                sensorSignificantMotionDetectorAdapter.updateSensors(sensorEvent);
                break;
            case Sensor.TYPE_PROXIMITY:
                sensorProximityDetectorAdapter.updateSensors(sensorEvent);
                break;
            case Sensor.TYPE_STATIONARY_DETECT:
                sensorStationaryDetectDetectorAdapter.updateSensors(sensorEvent);
                break;

        }

        //scrollDown();
    }

//    private void scrollDown() {
//
//        detected_sensors_accelerometer_listview.post(new Runnable() {
//            @Override
//            public void run() {
//                detected_sensors_accelerometer_listview.scrollTo(detected_sensors_accelerometer_listview.getScrollY(), detected_sensors_accelerometer_listview.getScrollY());
//            }
//        });
//    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_StopRecording) {
            Intent intent = new Intent(mContext, SensorRecorderService.class);
            stopService(intent);

            Utils.showToast(this, "Recording stopped. Relaunch the app to resume recording.");
            finish();
        }
    }
}
