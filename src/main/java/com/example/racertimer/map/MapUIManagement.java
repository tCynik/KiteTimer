package com.example.racertimer.map;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.racertimer.Instruments.CoursesCalculator;

public class MapUIManagement {
    private final static String PROJECT_LOG_TAG = "racer_timer_map_tools";

    private ConstraintLayout trackLayout;
    private ImageView directionArrow, windArrow;

    private float mapScale;
    private float minScale = 0.5f;
    private float maxScale = 10f;
    private final float stepScaleChanging = 0.5f;

    public MapUIManagement(float defaultMapScale) {
        this.mapScale = defaultMapScale;
    }

    public void setUIViews (ConstraintLayout trackLayout,
                            ImageView directionArrow, ImageView windArrow,
                            Button btnIncScale, Button btnDecScale) {
        this.trackLayout = trackLayout;
        onScaleChanged(mapScale);
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

    public void onWindChanged (int windDirection) {
        Log.i(PROJECT_LOG_TAG, "wind on map was changed to "+CoursesCalculator.invertCourse(windDirection) );
        windArrow.setRotation(CoursesCalculator.invertCourse(windDirection));
    }

    public void onDrawViewCreated (DrawView drawView) {
        if (trackLayout != null) {
            trackLayout.addView(drawView);
            drawView.setX(200);
            drawView.setY(200);
            Log.i(PROJECT_LOG_TAG, "new drawView was added into trackLayout ");
        } else Log.i(PROJECT_LOG_TAG, "trackline was not pasted into fragment - has no any trackLayout! ");
    }

    /** Scale management block */
    public void onScaleIncreased () {
        if (mapScale < maxScale) {
            mapScale = mapScale + stepScaleChanging;
            onScaleChanged(mapScale);
            Log.i(PROJECT_LOG_TAG, "map scale was increased to "+mapScale);
        }
    }

    public void onScaleDecreased () {
        if (mapScale > minScale) {
            mapScale = mapScale - stepScaleChanging;
            onScaleChanged(mapScale);
            Log.i(PROJECT_LOG_TAG, "map scale was decreased to "+mapScale);
        }
    }

    private void onScaleChanged (float updatedScale) {
        trackLayout.setScaleY(updatedScale);
        trackLayout.setScaleX(updatedScale);
    }
}
