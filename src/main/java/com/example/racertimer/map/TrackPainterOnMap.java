package com.example.racertimer.map;

import android.content.Context;
import android.location.Location;
import android.util.Log;

public class TrackPainterOnMap {
    private final static String PROJECT_LOG_TAG = "racer_timer_painter";

    private Context context;
    public DrawView drawView;
    private int trackAccuracy = 5; // точность прорисовки трека = 5й знак после запятой в координатах

    private boolean recordingInProgress = true; //false; // TODO: after testing set FALSE
    private TrackDrawerTranzister trackDrawerTranzister;

    private Location lastPaintedLocation = null;
    private Location currentLocation;
    private double startPointLongitude; // точка начала трека принимается как начало отсчета карты...
    private double startPointLatitude;  //      т.е. Х=0 У=0 в локальной системе отсчета

    public TrackPainterOnMap (TrackDrawerTranzister trackDrawerTranzister, Context context) {
        this.trackDrawerTranzister = trackDrawerTranzister;
        if (this.trackDrawerTranzister == null) Log.i(PROJECT_LOG_TAG, "!!! Tranziter is still null");
        this.context = context;
    }

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//    }

    public void beginNewTrackDrawing (Location location) {
        Log.i(PROJECT_LOG_TAG, "track painter is starting new track drawing");
        drawView = new DrawView(context);
        //setContentView(drawView);
        trackDrawerTranzister.setDrawView(drawView);

        setStartCoordinates(location);

        //TODO: setScreenCenterCoordinates()
        //  нужны координаты. как варик - сделать бродкастлистенер. Либо передавать их при вызове трека.
        //  либо сохранять постоянно и брать последние (используй lastLocation)

        recordingInProgress = true;
    }

    private void setScreenCenterCoordinates() {

    }

    public void endTrackDrawing() {
        recordingInProgress = false;
        lastPaintedLocation = null;
        //TODO: change the color/alfa of just painted track
        //  maby ask to save the track or not (if track time is to short)
    }

    public DrawView getDrawView() {
        if (drawView == null) Log.i(PROJECT_LOG_TAG, "draw view in the track painter is null!");

        Log.i(PROJECT_LOG_TAG, "sending drawView from TrackPainter");
        return drawView;
    }

    public void onLocatoinChanged(Location location) {
        Log.i(PROJECT_LOG_TAG, "new location in Track Painter, speed is: " +location.getSpeed());
        currentLocation = location;
        if (recordingInProgress) addLocationIntoTrack(location);
    }

/** блок приватных методов */
    private void setStartCoordinates(Location location) {
        startPointLatitude = location.getLatitude();
        startPointLongitude = location.getLongitude();
        setScreenCenterCoordinates();
    }

    private void addLocationIntoTrack (Location location) {
        int actualPointX = calculateLocalX(location);
        int actualPointY = calculateLocalY(location);
        Log.i(PROJECT_LOG_TAG, "new location coordinates is: X = "+actualPointX+", Y = "+actualPointY);

        if (drawView == null) {
            Log.i(PROJECT_LOG_TAG, "!!!!!!draw view is null!!!!! = ");
        } else {
            Log.i(PROJECT_LOG_TAG, "=====!!!!!!draw view is not null!!!!!====== ");

// TODO: не создается обьект Draw View!!!
            if (lastPaintedLocation == null) { // первая точка - ничего не рисуем, просто запоминаем
                setStartCoordinates(location); // TODO: метод должен вызываться либо при вызове нового трека, либо при создании первой точки, не и там, и там!
                drawView.setStartCoordinates(actualPointX, actualPointY);
            } else { // со второй точки начинаем рисовать
                drawView.drawNextPoint(actualPointX, actualPointY);
            }

        }
        lastPaintedLocation = location;

    }

    private int calculateLocalX (Location location) {
        double coordinateDifferent = location.getLongitude() - startPointLongitude;
        Log.i(PROJECT_LOG_TAG, "calculating different coord. X = "+coordinateDifferent+", pixels = "+calculatePixelFromCoordinate(coordinateDifferent));
        return calculatePixelFromCoordinate(coordinateDifferent);
    }

    private int calculateLocalY (Location location) {
        double coordinateDifferent = location.getLatitude() - startPointLatitude;
        return (calculatePixelFromCoordinate(coordinateDifferent))*-1; // -1 для инвертирования оси У
    }

    private int calculatePixelFromCoordinate (double coordinate) {
        int scaleMultiplier = 1;
        for (int i = 1; i < trackAccuracy; i ++) {
            scaleMultiplier = scaleMultiplier * 10;
        }
        return (int) coordinate * scaleMultiplier;

        //TODO: протестировать корректность подсчета
    }
}



// TODO: найти привязку канвы или окружения к конкретной вьюшке; залогировать код и дальше тестить всё
