package com.example.racertimer.map;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

public class TrackPainterOnMap {
    private final static String PROJECT_LOG_TAG = "racer_timer_painter";

    private Context context;
    public DrawView drawView;
    private final int trackAccuracy = 5; // точность прорисовки трека = 5й знак после запятой в координатах

    private boolean recordingInProgress = true; //false; // TODO: after testing set FALSE
    private ConstraintLayout tracksLayout;

    private Location lastPaintedLocation = null;
    private Location currentLocation;
    private double startPointLongitude; // точка начала трека принимается как начало отсчета карты...
    private double startPointLatitude;  //      т.е. Х=0 У=0 в локальной системе отсчета

    public TrackPainterOnMap (Context context) {
        this.context = context;
    }

    public void beginNewTrackDrawing (Location location) {
        Log.i(PROJECT_LOG_TAG, "track painter is starting new track drawing");
        drawView = new DrawView(context, location);
        //trackDrawerTranzister.setDrawView(drawView); !!!!!!
        tracksLayout.addView(drawView);
        setScreenCenterCoordinates();

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
        выставляем вьюшку трека по центру лайаута
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
            addLocationIntoTrack(location);
            moveScreenCenter(location);
        }
    }

/** блок приватных методов */
    private void setStartCoordinates(Location location) {
        startPointLatitude = location.getLatitude();
        startPointLongitude = location.getLongitude();
        setScreenCenterCoordinates();
    }

// todo: нужен рефакторинг. В трек отправляем локацию, дальше он сам высчитывает координаты следующей точки
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
                drawView.drawNextLine(actualPointX, actualPointY);
                // TODO: вот тут попробуем смещать drawView по мере отрисовки трека

            }
        }
        lastPaintedLocation = location;
    }

    private void moveScreenCenter(Location location) {
        int actualPointX = calculateLocalX(location)*-1; // нынешние координаты в системе координат лайаута
        int actualPointY = calculateLocalY(location)*-1;
        drawView.setX(actualPointX);
        drawView.setY(actualPointY);
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

    public void setTracksLayout(ConstraintLayout tracksLayout) {
        this.tracksLayout = tracksLayout;
    }
}

//TODO: при не начатом записи трека идет обращение к этому классу, некорректное. Нужно запретить обращение если запись не ведется.

// TODO: найти привязку канвы или окружения к конкретной вьюшке; залогировать код и дальше тестить всё

// TODO: отрисовку ранее загруженных треков производить методом canvas.drawLines("массив с координатами", paint) - см. урок 142
//
