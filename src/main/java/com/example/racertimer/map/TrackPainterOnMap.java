package com.example.racertimer.map;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

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
        trackDrawerTranzister.setDrawView(drawView);

        if (location != null) {
            setStartCoordinates(location);
            Toast.makeText(context, "Track recording started!", Toast.LENGTH_LONG).show();
        } else Toast.makeText(context, "GPS offline. Switch it ON to begin.", Toast.LENGTH_LONG).show();

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
        int actualPointX = calculateLocalX(location); // нынешние координаты в системе координат лайаута
        int actualPointY = calculateLocalY(location);
        Log.i(PROJECT_LOG_TAG, "new location coordinates is: X = "+actualPointX+", Y = "+actualPointY);

        if (drawView == null) {
            Log.i(PROJECT_LOG_TAG, "drawView is null");
        } else {
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
        int coordinatePixels = calculatePixelFromCoordinate(coordinateDifferent);
        Log.i(PROJECT_LOG_TAG, "calculating different coord. X = "+coordinateDifferent+", pixels = "+coordinatePixels);
        return coordinatePixels; // TODO: вот эти координаты почему-то получаются нулевые, хотя в логах все ок
    }

    private int calculateLocalY (Location location) {
        double coordinateDifferent = location.getLatitude() - startPointLatitude;
        int coordinatePixels = calculatePixelFromCoordinate(coordinateDifferent) * -1; // -1 для инвертирования оси У
        Log.i(PROJECT_LOG_TAG, "calculating different coord. Y = "+coordinateDifferent+", pixels = "+coordinatePixels);
        return coordinatePixels;
    }

    private int calculatePixelFromCoordinate (double coordinateDifferent) {
        int scaledCoordinates = 0;
        if (Math.abs((int)coordinateDifferent) < 1) { // костыль на случай косяков с GPS и внезапных перескоков на большие расстояния
            int scaleMultiplier = 1;
            for (int i = 1; i < trackAccuracy; i ++) {
                scaleMultiplier = scaleMultiplier * 10;
            }
            scaledCoordinates = (int) (coordinateDifferent * scaleMultiplier);
            Log.i(PROJECT_LOG_TAG, "scale = "+scaleMultiplier+", different = "+coordinateDifferent+", " +
                    "scaled coordinate = "+scaledCoordinates);
        }
        return scaledCoordinates;

        //TODO: протестировать корректность подсчета
    }
}

//TODO: при не начатом записи трека идет обращение к этому классу, некорректное. Нужно запретить обращение если запись не ведется.

// TODO: найти привязку канвы или окружения к конкретной вьюшке; залогировать код и дальше тестить всё
