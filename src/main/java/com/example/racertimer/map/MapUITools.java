package com.example.racertimer.map;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.racertimer.Instruments.CoursesCalculator;

public class MapUITools {
    private final static String PROJECT_LOG_TAG = "racer_timer_map_tools";

    private ConstraintLayout trackLayout;
    private ImageView directionArrow, windArrow;
    private Button btnDecScale, btnIncScale;

    private float mapScale;
    private float minScale = 0.5f;
    private float maxScale = 10f;
    private double stepScale = 0.5;

    public MapUITools (float defaultMapScale) {
        this.mapScale = defaultMapScale;
    }

    public void setUIViews (ConstraintLayout trackLayout,
                            ImageView directionArrow, ImageView windArrow,
                            Button btnIncScale, Button btnDecScale) {
        this.trackLayout = trackLayout;
        onScaleChanged(mapScale);
        this.directionArrow = directionArrow;
        this.windArrow = windArrow;
        this.btnIncScale = btnIncScale;
        this.btnDecScale = btnDecScale;

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

    public void onWindChanged (int windDirection) {
        Log.i(PROJECT_LOG_TAG, "wind on map was changed to "+CoursesCalculator.invertCourse(windDirection) );
        windArrow.setRotation(CoursesCalculator.invertCourse(windDirection));
    }

    public void onScaleIncreased () {
        if (mapScale < maxScale) {
            mapScale = mapScale + (float)stepScale;
            onScaleChanged(mapScale);
            Log.i(PROJECT_LOG_TAG, "map scale was increased to "+mapScale);
        }
    }

    public void onScaleDecreased () {
        if (mapScale > minScale) {
            mapScale = mapScale - (float)stepScale;
            onScaleChanged(mapScale);
            Log.i(PROJECT_LOG_TAG, "map scale was decreased to "+mapScale);
        }
    }

    private void onScaleChanged (float updatedScale) {
        trackLayout.setScaleY(updatedScale);
        trackLayout.setScaleX(updatedScale);
    }
}
