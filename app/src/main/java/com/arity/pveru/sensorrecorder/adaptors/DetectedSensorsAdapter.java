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
import android.hardware.SensorEvent;
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
public class DetectedSensorsAdapter extends ArrayAdapter<SensorEvent> {

    public DetectedSensorsAdapter(Context context,
                                  ArrayList<SensorEvent> detectedActivities) {
        super(context, 0, detectedActivities);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        SensorEvent sensorEvent = getItem(position);
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(
                    R.layout.detected_sensor, parent, false);
        }

        // Find the UI widgets.
        TextView tvName = (TextView) view.findViewById(R.id.detected_sensor_name);
        TextView tvValue = (TextView) view.findViewById(R.id.detected_sensor_value);


        // Populate widgets with values.
        if (sensorEvent != null) {
            tvName.setText(sensorEvent.sensor.getName() + " (Type: " + sensorEvent.sensor.getType() + ", Accuracy: " + sensorEvent.accuracy + " )");

            StringBuilder detail = new StringBuilder();
            for (float value : sensorEvent.values) {
                detail.append(value + ",");
            }
            detail.deleteCharAt(detail.length() - 1);
            tvValue.setText(detail);
        }
        return view;
    }

    /**
     * Process list of recently detected activities and updates the list of {@code DetectedActivity}
     * objects backing this adapter.
     *
     * @param sensorEvent the freshly detected location
     */
    public void updateSensors(SensorEvent sensorEvent) {

        // Remove all items.
        //if (this.getCount() > 200) {
        clear();
        //}

        // Adding the new list items notifies attached observers that the underlying data has
        // changed and views reflecting the data should refresh.
        this.add(sensorEvent);

    }
}