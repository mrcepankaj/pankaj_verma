package com.arity.pveru.sensorrecorder.sensors.activity;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.arity.pveru.sensorrecorder.interfaces.IActivitySensorUpdateListener;
import com.arity.pveru.sensorrecorder.common.Constants;
import com.arity.pveru.sensorrecorder.common.ExecutorHelper;
import com.arity.pveru.sensorrecorder.common.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Class Handle the registration and un-registration of activity recognition with google play service.
 * This class will finally tell the driving engine when a driving activity is detected.
 */
public class ActivityDetectionHelper {

    private final Context mContext;
    private GoogleApiClient mARClient;
    private PendingIntent mPendingIntent;

    private Timer mTimer;
    private boolean isStarted;
    private boolean isReceiverRegistered;
    private GoogleApiClient.OnConnectionFailedListener connectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {

        @Override
        public void onConnectionFailed(ConnectionResult arg0) {
            onActivityRecognitionDisconnectedOrFailed(null, arg0);
        }
    };

    private BroadcastReceiver mActivityDetectionReceiver = new ActivityDetectionReceiver();

    static IActivitySensorUpdateListener iSensorUpdateListener;

    public static void setUpdateListener(IActivitySensorUpdateListener listener) {
        iSensorUpdateListener = listener;
    }

    private GoogleApiClient.ConnectionCallbacks connectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {

        @Override
        public void onConnected(Bundle bundle) {

            Utils.putLog("Activity Recognition Helper : Google Play Service Connected");

            try {
                Intent intent = new Intent();
                intent.setAction(Constants.ACTION_ACTIVITY_DETECTED);
                mPendingIntent = PendingIntent
                        .getBroadcast(
                                mContext,
                                Constants.ACTIVITY_RECOGNITION_REQUEST_CODE,
                                intent, PendingIntent.FLAG_UPDATE_CURRENT);
                mContext.registerReceiver(mActivityDetectionReceiver, new IntentFilter(Constants.ACTION_ACTIVITY_DETECTED));
                isReceiverRegistered = true;
                ActivityRecognition.ActivityRecognitionApi
                        .requestActivityUpdates(mARClient, Constants.DETECTION_INTERVAL_IN_MILLISECONDS, mPendingIntent);
            } catch (Exception e) {

                onActivityRecognitionDisconnectedOrFailed(e.getLocalizedMessage(), null);
                Utils.putErrorLog("onConnected Exception :" + e.getLocalizedMessage());
            }
        }

        @Override
        public void onConnectionSuspended(int i) {
            Utils.putErrorLog("onConnectionSuspended Activity Recognition Helper : Google Play Service Connection Suspended");
        }
    };


    /**
     * Constructor
     *
     * @param context Application Context
     */
    public ActivityDetectionHelper(Context context) {
        mContext = context;
    }

    /**
     * Start activity recognition.
     * The system will try to connect to Google play service to fetch activity recognition updates.
     * Once the google play service is connected, the system will route the activity recognition updates
     * to the intent service class which is passed in the constructor to process the recognition updates.
     * If the system fails to connect to the Google Play Service, it will retry after waiting for 2 minutes
     * as per default configuration.
     */
    public void startActivityRecognition() {
        if (!isStarted) {
            Utils.putLog("ActivityDetectionHelper startActivityRecognition Activity Recognition Helper : Started Recognition");
            mARClient = new GoogleApiClient.Builder(mContext)
                    .addApi(ActivityRecognition.API)
                    .addConnectionCallbacks(connectionCallbacks)
                    .addOnConnectionFailedListener(connectionFailedListener)
                    .build();
            mARClient.connect();
            isStarted = true;
        }
    }


    private void onActivityRecognitionDisconnectedOrFailed(String exceptionMessage, ConnectionResult ConnectionFailedObj) {
        Utils.putErrorLog("ActivityDetectionHelper onActivityRecognitionDisconnectedOrFailed Activity Recognition Helper : Activity Recognition Disconnected or failed!! " + exceptionMessage);

        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
        }


        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Utils.putLog("ActivityDetectionHelper onActivityRecognitionDisconnectedOrFailed Activity Recognition Helper : Restarting Recognition");
                startActivityRecognition();
            }
        }, Constants.ACTIVITY_RECOGNITION_RESTART_INTERVAL);
    }

    /**
     * Stops activity recognition.
     * System will disconnect from the Google Play Services and will not process any more further updates.
     */
    public void stopActivityRecognition() {

        if (isStarted) {
            Utils.putLog("ActivityDetectionHelper stopActivityRecognition - Stopped Recognition");
            unregisterForActivityRecognition();
            isStarted = false;
        }

    }

    private synchronized void unregisterForActivityRecognition() {
        Utils.putLog("ActivityDetectionHelper unregisterForActivityRecognition Unregister from Activity Recognition!!");
        if (mARClient != null) {
            if (mARClient.isConnected()) {
                try {
                    if (mPendingIntent != null) {
                        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(mARClient, mPendingIntent);
                        mARClient.disconnect();
                    }
                    mARClient.disconnect();
                } catch (IllegalStateException e) {
                    Utils.putErrorLog("ActivityDetectionHelper unregisterForActivityRecognition: " + e.getLocalizedMessage());

                }
            }

            if (mARClient.isConnectionCallbacksRegistered(connectionCallbacks)) {
                mARClient.unregisterConnectionCallbacks(connectionCallbacks);
            }

            if (mARClient.isConnectionFailedListenerRegistered(connectionFailedListener)) {
                mARClient.unregisterConnectionFailedListener(connectionFailedListener);
            }
        }

        if (isReceiverRegistered) {
            mContext.unregisterReceiver(mActivityDetectionReceiver);
            isReceiverRegistered = false;
        }
        mARClient = null;
    }

    /**
     * Gets whether the activity recognition has started.
     *
     * @return true if the activity recognition has already started.
     */
    public boolean hasStartedRecognition() {
        return isStarted;
    }


    public final class ActivityDetectionReceiver extends BroadcastReceiver {

        ActivityRecognitionResult result = null;

        @Override
        public void onReceive(Context context, final Intent intent) {


            if (ActivityRecognitionResult.hasResult(intent)) {
                result = ActivityRecognitionResult
                        .extractResult(intent);

                if (iSensorUpdateListener != null)
                    iSensorUpdateListener.onActivityUpdate(result);

                ExecutorHelper.getExecutorInstance("GenericExecutor").execute(new Runnable() {
                    @Override
                    public void run() {

                        ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();
                        Utils.putActivityToFile(Calendar.getInstance().getTimeInMillis(), detectedActivities);
                    }
                });
            }

        }

    }

}
