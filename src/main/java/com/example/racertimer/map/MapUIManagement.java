package com.example.racertimer.map;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.racertimer.Instruments.CoursesCalculator;

public class MapUIManagement {
    private final static String PROJECT_LOG_TAG = "racer_timer_map_tools";

    private ImageView directionArrow, windArrow;

    private MapManager mapManager;

    private float mapScale;
    private float minScale = 0.5f;
    private float maxScale = 10f;
    private final float stepScaleChanging = 0.5f;

    public MapUIManagement(float defaultMapScale) {
        this.mapScale = defaultMapScale;
    }

    public void setUIViews (ImageView directionArrow, ImageView windArrow,
                            Button btnIncScale, Button btnDecScale) {
        //onScaleChanged(mapScale);
        this.directionArrow = directionArrow;
        this.windArrow = windArrow;

        btnIncScale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onScaleIncreased();
            }
        });

        btnDecScale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onScaleDecreased();
            }
        });
    }

    public void onBearingChanged (int bearing) {
        Log.i(PROJECT_LOG_TAG, "bearing on map was changed to "+bearing);
        if (directionArrow != null) directionArrow.setRotation(bearing);
    }

    public void setWindArrowDirection(int windDirection) {
        Log.i(PROJECT_LOG_TAG, "wind on map was changed to "+CoursesCalculator.invertCourse(windDirection) );
        windArrow.setRotation(CoursesCalculator.invertCourse(windDirection));
    }

    public void setMapManager(MapManager mapManager) {
        this.mapManager = mapManager;
    }

    /** Scale management block */
    private void onScaleIncreased () {
        if (mapScale < maxScale) {
            mapScale = mapScale + stepScaleChanging;
            onScaleChanged(mapScale);
            Log.i(PROJECT_LOG_TAG, "map scale was increased to "+mapScale);
        }
    }

    private void onScaleDecreased () {
        if (mapScale > minScale) {
            mapScale = mapScale - stepScaleChanging;
            onScaleChanged(mapScale);
            Log.i(PROJECT_LOG_TAG, "map scale was decreased to "+mapScale);
        }
    }

    private void onScaleChanged (float updatedScale) {
        mapManager.onScaleChanged(updatedScale);

    }
}
