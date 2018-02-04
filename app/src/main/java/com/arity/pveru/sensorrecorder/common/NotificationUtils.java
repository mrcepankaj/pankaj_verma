package com.arity.pveru.sensorrecorder.common;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;

public class NotificationUtils extends ContextWrapper {

    private NotificationManager mManager;
    public static final String NOTIFICATION_CHANNEL_ID = "com.arity.pveru.sensorrecorder";
    public static final String NOTIFICATION_CHANNEL_NAME = "NOTIFICATION CHANNEL";

    public NotificationUtils(Context base) {
        super(base);
        createChannel();
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void createChannel() {
        // create android channel
        NotificationChannel androidChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        getNotificationManager().createNotificationChannel(androidChannel);

    }

    public NotificationManager getNotificationManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getChannelNotification(String contentTitle, String contentText, PendingIntent launcherIntent, int smallIcon) {
        return new Notification.Builder(this, NotificationUtils.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(smallIcon)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setContentIntent(launcherIntent);

    }

}
