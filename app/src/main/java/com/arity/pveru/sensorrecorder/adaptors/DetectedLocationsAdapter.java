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

package com.arity.pveru.sensorrecorder.adaptors;

import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.arity.pveru.sensorrecorder.R;
import com.arity.pveru.sensorrecorder.common.Constants;
import com.arity.pveru.sensorrecorder.common.Utils;

import java.util.ArrayList;

/**
 * Adapter that is backed by an array of {@code DetectedActivity} objects. Finds UI elements in the
 * detected_activity layout and populates each element with data from a DetectedActivity
 * object.
 */
public class DetectedLocationsAdapter extends ArrayAdapter<Location> {

    public DetectedLocationsAdapter(Context context,
                                    ArrayList<Location> detectedActivities) {
        super(context, 0, detectedActivities);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        Location location = getItem(position);
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(
                    R.layout.detected_location, parent, false);
        }

        // Find the UI widgets.
        TextView tvDateTimeStamp = (TextView) view.findViewById(R.id.detected_location_timestamp);
        TextView tvLongitude = (TextView) view.findViewById(R.id.detected_location_longitude);
        TextView tvLatitude = (TextView) view.findViewById(R.id.detected_location_latitude);
        TextView tvAccu = (TextView) view.findViewById(R.id.detected_location_accuracy);
        TextView tvVerticleAccu = (TextView) view.findViewById(R.id.detected_location_verticle_accuracy);
        TextView tvAltitude = (TextView) view.findViewById(R.id.detected_location_altitude);
        TextView tvBearing = (TextView) view.findViewById(R.id.detected_location_bearing);
        TextView tvSpeed = (TextView) view.findViewById(R.id.detected_location_speed);


        // Populate widgets with values.
        if (location != null) {
            tvDateTimeStamp.setText(Utils.getTimeW3C(location.getTime(), Constants.DATE_FORMAT_YYYY_MM_DD_T_HH_MM_SSZ));
            tvAccu.setText("" + location.getAccuracy());
            tvAltitude.setText("" + location.getAltitude());
            tvSpeed.setText("" + Utils.convertMpsToMph(location.getSpeed()));
            tvBearing.setText("" + location.getBearing());
            tvLatitude.setText("" + location.getLatitude());
            tvLongitude.setText("" + location.getLongitude());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                tvVerticleAccu.setText("" + location.getVerticalAccuracyMeters());
            } else {
                tvVerticleAccu.setText("- NA-");
            }
        }
        return view;
    }

    /**
     * Process list of recently detected activities and updates the list of {@code DetectedActivity}
     * objects backing this adapter.
     *
     * @param location the freshly detected location
     */
    public void updateLocations(Location location) {

        // Remove all items.
        this.clear();

        // Adding the new list items notifies attached observers that the underlying data has
        // changed and views reflecting the data should refresh.
        this.add(location);

    }
}