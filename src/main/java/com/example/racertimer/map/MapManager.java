package com.example.racertimer.map;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

public class MapManager {
    private final static String PROJECT_LOG_TAG = "racer_timer_painter";

    private Context context;
    private final int trackAccuracy = 5; // точность прорисовки трека = 5й знак после запятой в координатах

    public DrawView drawView;
    TrackGridCalculator trackGridCalculator;
    private ScreenWindowShifter screenWindowShifter;
    private ArrowMover arrowMover;

    private double scale = 1;
    private boolean screenCenterPinnedOnPosition = true;
    private boolean scrollingIsManual = true;

    private boolean recordingInProgress = false; // TODO: after testing set FALSE
    private ConstraintLayout tracksLayout;
    private ScrollView windowMap;
    private HorizontalScrollView horizontalMapScroll;
    private ImageButton btnFixPosition;
    private ImageView arrowPosition;

    private Location currentLocation;

    public MapManager(Context context) {
        this.context = context;
        trackGridCalculator = new TrackGridCalculator(this);

    }

    public void beginNewTrackDrawing (Location location) {
        Log.i(PROJECT_LOG_TAG, "Map Manager is starting new track drawing");
        trackGridCalculator.onTrackStarted(location);

        drawView = new DrawView(context, trackGridCalculator);
        tracksLayout.addView(drawView);
        arrowPosition.bringToFront();

        drawView.setMapManager(this); // TODO: what will be when location = null?!! service the case!
        Log.i(PROJECT_LOG_TAG, "view sizes: X ="+drawView.getWidth()+", Y ="+drawView.getHeight());

        screenWindowShifter = new ScreenWindowShifter(this, location, trackGridCalculator, tracksLayout,  windowMap, horizontalMapScroll, scale);
        if (location != null) {
            //setStartCoordinates(location);
            Toast.makeText(context, "Track recording started!", Toast.LENGTH_LONG).show();
        } else Toast.makeText(context, "GPS offline. Switch it ON to begin.", Toast.LENGTH_LONG).show();

        recordingInProgress = true;
    }

    public void setScreenCenterToView () {
        float screenCenterX = (tracksLayout.getWidth() / 2);
        float screenCenterY = (tracksLayout.getHeight() / 2);
        float windowCenterX = windowMap.getWidth() / 2;
        float windowCenterY = windowMap.getHeight() / 2;
        drawView.setScreenCenterCoordinates(screenCenterX, screenCenterY, windowCenterX, windowCenterY);
    }

    public void endTrackDrawing() {
        recordingInProgress = false;
        //TODO: change the color/alfa of just painted track
        //  maby ask to save the track or not to save (if track time is to short)

    }

    public void onLocatoinChanged(Location location) {
        Log.i(PROJECT_LOG_TAG, "new location in Track Painter, speed is: " +location.getSpeed());
        currentLocation = location;
        if (recordingInProgress) {
            drawView.onLocationChanged(location);

            if (screenCenterPinnedOnPosition) {
                scrollingIsManual = false;
                screenWindowShifter.moveWindowCenterToPosition(location);
                scrollingIsManual = true;
            }
        }
        arrowMover.moveArrowToPosition(location);
    }

    public void setTracksLayout(ScrollView windowMap, HorizontalScrollView horizontalMapScroll,
                                ConstraintLayout tracksLayout, ImageButton btnFixPosition, ImageView arrowPosition) {
        this.tracksLayout = tracksLayout;
        this.arrowPosition = arrowPosition;
        trackGridCalculator.setTracksLayout(tracksLayout);

        windowMap.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (screenCenterPinnedOnPosition) {
                    if (scrollingIsManual) {
                        screenCenterPinnedOnPosition = false;
                        btnFixPosition.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        this.windowMap = windowMap;

        horizontalMapScroll.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (screenCenterPinnedOnPosition) {
                    if (scrollingIsManual) {
                        screenCenterPinnedOnPosition = false;
                        btnFixPosition.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        this.horizontalMapScroll = horizontalMapScroll;

        this.btnFixPosition = btnFixPosition;
        this.btnFixPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screenWindowShifter.moveWindowCenterToPosition(currentLocation);
                screenCenterPinnedOnPosition = true;
                btnFixPosition.setVisibility(View.INVISIBLE);
            }
        });

        arrowMover = new ArrowMover(this, arrowPosition, trackGridCalculator);
    }

    public void onScaleChanged (double scale) {
        scrollingIsManual = false;
        if (screenCenterPinnedOnPosition) {
            screenWindowShifter.onScaleChanged(scale);
        }

        tracksLayout.setScaleX((float)scale);
        tracksLayout.setScaleY((float)scale);
        scrollingIsManual = true;
    }

    public void setWindowSizesToShifter() {
        int windowX = windowMap.getWidth();
        int windowY = windowMap.getHeight();
        screenWindowShifter.setWindowSizes(windowX, windowY);
    }

    public void onFixButtonPressed() {
        if (screenCenterPinnedOnPosition) {
            screenCenterPinnedOnPosition = false;
        }
    }
}
//Log.i("bugfix", "fixPosition is working2. pinned = "+ screenCenterPinnedOnPosition );


//TODO: разобраться с алгоритмом начала запука трека (совместно с таймером)

// TODO: сделать сохранение точек трека в массив. Сделать сохранение и загрузку треков. Сделать отображении сохраненных треков
//  отрисовку ранее загруженных треков производить методом canvas.drawLines("массив с координатами", paint) - см. урок 142

// TODO: переработать отображение маркера позиции. Маркер - не по центру экрана, а на текущей позиции.
//  Расчет позиции маркера - в отдельном классе
//
