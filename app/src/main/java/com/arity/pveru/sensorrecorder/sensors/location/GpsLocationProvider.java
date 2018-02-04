package com.arity.pveru.sensorrecorder.sensors.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.arity.pveru.sensorrecorder.interfaces.IActivitySensorUpdateListener;
import com.arity.pveru.sensorrecorder.common.Utils;
import com.arity.pveru.sensorrecorder.interfaces.ILocationSensorUpdateListeners;

import java.util.Calendar;


public class GpsLocationProvider implements android.location.LocationListener {

    private final LocationManager mLocationManager;

    private Context mContext;

    public GpsLocationProvider(Context context) {

        mContext = context;
        mLocationManager = (LocationManager) mContext
                .getSystemService(Context.LOCATION_SERVICE);
    }

    static ILocationSensorUpdateListeners iSensorUpdateListener;

    public static void setUpdateListener(ILocationSensorUpdateListeners listener) {
        iSensorUpdateListener = listener;
    }


    public void startLocationFetch() {

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 1000, 0,
                this);

    }

    public void stopLocationFetch() {
        if (mLocationManager != null)
            mLocationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        //Utils.processLocationUpdate(mContext, location);

        //Location locationBean = Utils.getLocationBeanFromLocation(location);

        if (iSensorUpdateListener != null)
            iSensorUpdateListener.onLocationUpdate(location);
        /*PreferenceManager.getDefaultSharedPreferences(mContext)
                .edit()
                .putString(Constants.KEY_DETECTED_LOCATIONS,
                        Utils.detectedLocationToJson(locationBean))
                .apply();*/

        Utils.putLocationToFile(Calendar.getInstance().getTimeInMillis(), location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
