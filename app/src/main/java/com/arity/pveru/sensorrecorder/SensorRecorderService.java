package com.arity.pveru.sensorrecorder;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;

import com.arity.pveru.sensorrecorder.common.Constants;
import com.arity.pveru.sensorrecorder.common.ExecutorHelper;
import com.arity.pveru.sensorrecorder.common.FileManager;
import com.arity.pveru.sensorrecorder.common.NotificationUtils;
import com.arity.pveru.sensorrecorder.common.Utils;
import com.arity.pveru.sensorrecorder.sensors.activity.ActivityDetectionHelper;
import com.arity.pveru.sensorrecorder.sensors.location.GpsLocationProvider;
import com.arity.pveru.sensorrecorder.sensors.sensors.SensorHelper;

import java.io.File;
import java.util.Calendar;

public class SensorRecorderService extends Service {

    private static final int NOTIFICATION_ID = 20182018;
    private static final int NOTIFICATION_REQUEST_CODE = 1000;
    private static final String NOTIFICATION_DEFAULT_DESCRIPTION = "Tap here for more information.";

    ActivityDetectionHelper mActivityRecognitionHelper;
    GpsLocationProvider gpsLocationProvider;
    SensorHelper sensorHelper;

    GpsSwitchMonitor gpsSwitchMonitor;


    public SensorRecorderService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        //// TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        try {

            Notification notification;
            PackageManager packageManager = getPackageManager();
            Intent launcherIntent = packageManager != null && !TextUtils.isEmpty(getPackageName()) ?
                    packageManager.getLaunchIntentForPackage(getPackageName()) :
                    new Intent();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                NotificationUtils mNotificationUtils = new NotificationUtils(this);
                notification = mNotificationUtils.
                        getChannelNotification("Sensor Recorder is running", NOTIFICATION_DEFAULT_DESCRIPTION, PendingIntent.getActivity(this, NOTIFICATION_REQUEST_CODE,
                                launcherIntent != null ? launcherIntent : new Intent(),
                                PendingIntent.FLAG_UPDATE_CURRENT), android.R.color.background_light)
                        .build();

            } else {
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(android.R.color.background_light)
                        .setContentTitle("Sensor Recorder is running")
                        .setContentText(NOTIFICATION_DEFAULT_DESCRIPTION)
                        .setContentIntent(PendingIntent.getActivity(this, NOTIFICATION_REQUEST_CODE,
                                launcherIntent != null ? launcherIntent : new Intent(),
                                PendingIntent.FLAG_UPDATE_CURRENT));
                notification = notificationBuilder.build();
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                notificationManager.notify(NOTIFICATION_ID, notification);
            }
            startForeground(NOTIFICATION_ID, notification);

            initFilesAndFolders();
            initActivity();
            initLocation();
            initSensors();

            startMonitors();

            Utils.showToast(this, "Started SensorRecorderService successfully");
            Utils.putLog("Started SensorRecorderService successfully");
        } catch (Exception ex) {
            Utils.showToast(this, "SensorRecorderService exception: " + ex.getLocalizedMessage());
            Utils.putErrorLog("SensorRecorderService exception: " + ex.getLocalizedMessage());
        }
    }


    private void initActivity() {

        mActivityRecognitionHelper = new ActivityDetectionHelper(this);

        if (!mActivityRecognitionHelper.hasStartedRecognition()) {
            Utils.putLog("SensorRecorderService startActivityRecognition.");
            mActivityRecognitionHelper.startActivityRecognition();
        }
    }

    private void initSensors() {
        sensorHelper = new SensorHelper(getApplicationContext());
        sensorHelper.startSensorRecording();
    }

    private void initLocation() {
        gpsLocationProvider = new GpsLocationProvider(this);
        gpsLocationProvider.startLocationFetch();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        destroyActivity();
        destroyLocation();

        if (sensorHelper != null)
            sensorHelper.stopSensorRecording();

        stopMonitors();

        super.onDestroy();
    }

    private void destroyActivity() {
        if (mActivityRecognitionHelper != null) {
            mActivityRecognitionHelper.stopActivityRecognition();
        }
    }

    private void destroyLocation() {
        if (gpsLocationProvider != null) {
            gpsLocationProvider.stopLocationFetch();
        }

    }

    private void initFilesAndFolders() {

        Constants.BASE_PATH = getExternalFilesDir(null) + File.separator + "SensorRecorder" + File.separator;

        File debugBaseFolder = new File(Constants.BASE_PATH);

        if (!debugBaseFolder.exists())
            debugBaseFolder.mkdirs();

        //Log file
        File logFile = new File(Constants.FilePath.LOGS);
        if (!logFile.exists())
            FileManager.getInstance(Constants.FilePath.LOGS, ExecutorHelper.getLogExecutorInstance()).writeData("LOGS BEGINS HERE ... .... \n\n", true);

        //App file
        File fileFile = new File(Constants.FilePath.APPINFO);
        if (!fileFile.exists())
            FileManager.getInstance(Constants.FilePath.APPINFO, ExecutorHelper.getLogExecutorInstance()).writeData(Constants.FileHeaders.APPINFO + "\n", true);

        //Activity file with header
        File activityFile = new File(Constants.FilePath.ACTIVITY);
        if (!activityFile.exists())
            FileManager.getInstance(Constants.FilePath.ACTIVITY, ExecutorHelper.getActivityExecutorInstance()).writeData(Constants.FileHeaders.ACTIVITY + "\n", true);

        //Location file with header
        File locationFile = new File(Constants.FilePath.LOCATION);
        if (!locationFile.exists())
            FileManager.getInstance(Constants.FilePath.LOCATION, ExecutorHelper.getLocationExecutorInstance()).writeData(Constants.FileHeaders.LOCATION + "\n", true);

    }

    private void startMonitors() {
        gpsSwitchMonitor = new GpsSwitchMonitor(this);
        gpsSwitchMonitor.start();
    }

    private void stopMonitors() {
        if (gpsSwitchMonitor != null)
            gpsSwitchMonitor.stop();
    }


    private class GpsSwitchMonitor {

        /*Private Members*/
        private boolean hasRegistered;
        private Context context;


        public GpsSwitchMonitor(Context mContext) {
            this.context = mContext;
        }

        private BroadcastReceiver gpsSwitchMonitorBroadCastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (!Utils.isGpsEnabled(context)) {
                    Utils.putLog("GpsSwitchMonitor exception: GPS turned off at: " + Utils.getTimeW3C(Calendar.getInstance().getTime().getTime(),
                            Constants.DATE_FORMAT_YYYY_MM_DD_T_HH_MM_SSZ));
                    //Utils.showToast(context, "Turn on location and launch the app again.");
                    stopSelf();
                    //SensorRecorderService.this.getApplication().getActivity().finish();
                    System.exit(0);
                }
            }
        };


        public void start() {
            if (!hasRegistered) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    context.registerReceiver(gpsSwitchMonitorBroadCastReceiver, new IntentFilter(LocationManager.MODE_CHANGED_ACTION));
                } else {
                    context.registerReceiver(gpsSwitchMonitorBroadCastReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
                }
                hasRegistered = true;
            }
        }

        public void stop() {
            if (hasRegistered) {
                context.unregisterReceiver(gpsSwitchMonitorBroadCastReceiver);
                hasRegistered = false;
            }
        }
    }


}
