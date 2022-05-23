package com.example.racertimer.map;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

public class TrackPainterOnMap {
    private final static String PROJECT_LOG_TAG = "racer_timer_painter";

    private Context context;
    public DrawView drawView;
    private final int trackAccuracy = 5; // точность прорисовки трека = 5й знак после запятой в координатах

    private boolean recordingInProgress = false; // TODO: after testing set FALSE
    private ConstraintLayout tracksLayout;

    private Location lastPaintedLocation = null;
    private Location currentLocation;
    private double startPointLongitude; // точка начала трека принимается как начало отсчета карты...
    private double startPointLatitude;  //      т.е. Х=0 У=0 в локальной системе отсчета

    private float layoutCenterCoordinateX;
    private float layoutCenterCoordinateY;

    public TrackPainterOnMap (Context context) {
        this.context = context;
    }

    public void beginNewTrackDrawing (Location location) {
        Log.i(PROJECT_LOG_TAG, "track painter is starting new track drawing");
        drawView = new DrawView(context, location);
        tracksLayout.addView(drawView);
        drawView.setTrackPainterOnMap(this);
        float screenCenterX = (tracksLayout.getWidth() / 2);
        float screenCenterY = (tracksLayout.getHeight() / 2);
        drawView.setScreenCenterCoordinates(screenCenterX, screenCenterY);
        drawView.setBackgroundColor(Color.GRAY);
        Log.i(PROJECT_LOG_TAG, "view sizes: X ="+drawView.getWidth()+", Y ="+drawView.getHeight());

        if (location != null) {
            //setStartCoordinates(location);
            Toast.makeText(context, "Track recording started!", Toast.LENGTH_LONG).show();
        } else Toast.makeText(context, "GPS offline. Switch it ON to begin.", Toast.LENGTH_LONG).show();

        //TODO: setScreenCenterCoordinates()
        //  нужны координаты. как варик - сделать бродкастлистенер. Либо передавать их при вызове трека.
        //  либо сохранять постоянно и брать последние (используй lastLocation)

        recordingInProgress = true;
    }

    public void setScreenToCoordinates (float coordX, float coordY) {
        drawView.setX(layoutCenterCoordinateX - coordX);
        drawView.setY(layoutCenterCoordinateY - coordY);
    }

    public void moveBorderX(double volume) {

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
        }
    }

    public void setTracksLayout(ConstraintLayout tracksLayout) {
        this.tracksLayout = tracksLayout;
    }
}

//TODO: при не начатом записи трека идет обращение к этому классу, некорректное. Нужно запретить обращение если запись не ведется.

// TODO: найти привязку канвы или окружения к конкретной вьюшке; залогировать код и дальше тестить всё

// TODO: отрисовку ранее загруженных треков производить методом canvas.drawLines("массив с координатами", paint) - см. урок 142
//
