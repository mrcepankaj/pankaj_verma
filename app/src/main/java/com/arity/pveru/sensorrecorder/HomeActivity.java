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
    ListView detectedSensorsListView;

    /**
     * Adapter backed by a list of DetectedActivity objects.
     */
    private DetectedActivitiesAdapter activitiesAdapter;
    private DetectedLocationsAdapter locationsAdapter;
    private DetectedSensorsAdapter sensorAdapter;

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
                Utils.showToast(this, "Enable location services and launch the app again");
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

        detectedSensorsListView = (ListView) findViewById(
                R.id.detected_sensors_listview);


        // Bind the adapter to the ListView responsible for display data for detected activities.
        ArrayList<DetectedActivity> detectedActivities = new ArrayList<>();
        activitiesAdapter = new DetectedActivitiesAdapter(this, detectedActivities);
        detectedActivitiesListView.setAdapter(activitiesAdapter);

        // Bind the adapter to the ListView responsible for display data for detected locations.
        ArrayList<Location> locations = new ArrayList<>();
        locationsAdapter = new DetectedLocationsAdapter(this, locations);
        detectedLocationsListView.setAdapter(locationsAdapter);

        ArrayList<SensorEvent> sensors = new ArrayList<>();
        sensorAdapter = new DetectedSensorsAdapter(this, sensors);
        detectedSensorsListView.setAdapter(sensorAdapter);
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
        sensorAdapter.updateSensors(sensorEvent);
        scrollDown();
    }

    private void scrollDown() {

        detectedSensorsListView.post(new Runnable() {
            @Override
            public void run() {
                detectedSensorsListView.scrollTo(detectedSensorsListView.getScrollY(), detectedSensorsListView.getScrollY());
            }
        });
    }

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
