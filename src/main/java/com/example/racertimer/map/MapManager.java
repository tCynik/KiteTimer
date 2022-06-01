package com.example.racertimer.map;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

public class MapManager {
    private final static String PROJECT_LOG_TAG = "racer_timer_painter";

    private Context context;
    public DrawView drawView;
    private final int trackAccuracy = 5; // точность прорисовки трека = 5й знак после запятой в координатах

    private ScreenWindowShifter screenWindowShifter;
    private double scale = 1;
    private boolean screenCenterPinnedOnPosition = true;


    private boolean recordingInProgress = false; // TODO: after testing set FALSE
    private ConstraintLayout tracksLayout;
    private ScrollView windowMap;

    private Location lastPaintedLocation = null;
    private Location currentLocation;

    public MapManager(Context context) {
        this.context = context;
    }

    public void beginNewTrackDrawing (Location location) {
        Log.i(PROJECT_LOG_TAG, "track painter is starting new track drawing");
        drawView = new DrawView(context, location);
        tracksLayout.addView(drawView);
        drawView.setTrackPainterOnMap(this); // TODO: what will be when location = null!!!!
//        drawView.setBorderShiftStep(tracksLayout.getWidth() / 2);
        drawView.setBackgroundColor(Color.GRAY);
        Log.i(PROJECT_LOG_TAG, "view sizes: X ="+drawView.getWidth()+", Y ="+drawView.getHeight());

        screenWindowShifter = new ScreenWindowShifter(this, location, tracksLayout, scale);
        if (location != null) {
            //setStartCoordinates(location);
            Toast.makeText(context, "Track recording started!", Toast.LENGTH_LONG).show();
        } else Toast.makeText(context, "GPS offline. Switch it ON to begin.", Toast.LENGTH_LONG).show();

        //TODO: setScreenCenterCoordinates()
        //  нужны координаты. как варик - сделать бродкастлистенер. Либо передавать их при вызове трека.
        //  либо сохранять постоянно и брать последние (используй lastLocation)

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
        lastPaintedLocation = null;
        //TODO: change the color/alfa of just painted track
        //  maby ask to save the track or not (if track time is to short)

    }

    public void onLocatoinChanged(Location location) {
        Log.i(PROJECT_LOG_TAG, "new location in Track Painter, speed is: " +location.getSpeed());
        currentLocation = location;
        if (recordingInProgress) {
            drawView.onLocationChanged(location);
            if (screenCenterPinnedOnPosition) screenWindowShifter.moveWindowCenterToPosition(location);
        }
    }

    public void setTracksLayout(ScrollView windowMap, ConstraintLayout tracksLayout) {
        this.tracksLayout = tracksLayout;
        this.windowMap = windowMap;
    }

    public void onScaleChanged (double scale) {
        screenWindowShifter.onScaleChanged(scale);
        tracksLayout.setScaleX((float)scale);
        tracksLayout.setScaleY((float)scale);
    }

    public void setWindowSizesToShifter() {
        int windowX = windowMap.getWidth();
        int windowY = windowMap.getHeight();
        screenWindowShifter.setWindowSizes(windowX, windowY);
    }
}

//TODO: разобраться с алгоритмом начала запука трека (совместно с таймером)

/** утро 02.06:
// TODO: сделать сохранение точек трека в массив. Сделать сохранение и загрузку треков. Сделать отображении сохраненных треков
//  отрисовку ранее загруженных треков производить методом canvas.drawLines("массив с координатами", paint) - см. урок 142
*/

// TODO: переработать отображение маркера позиции. Маркер - не по центру экрана, а на текущей позиции.
//  Расчет позиции маркера - в отдельном классе
//
