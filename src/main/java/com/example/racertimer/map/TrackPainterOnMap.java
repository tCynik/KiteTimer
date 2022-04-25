package com.example.racertimer.map;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

public class TrackPainterOnMap extends Activity {
    private final static String PROJECT_LOG_TAG = "racer_timer_painter";

    private DrawView drawView;
    private int trackAccuracy = 5; // точность прорисовки трека = 5й знак после запятой в координатах

    private Location lastLocation = null;
    private double startPointLongitude; // точка начала трека принимается как начало отсчета карты...
    private double startPointLatitude;  // т.е. Х=0 У=0 в локальной системе отсчета

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        drawView = new DrawView(this);
        setContentView(drawView);
        // TODO: сейчас новая вьюшка рисоваия трека создается при создании класса TrackPainterMap.
        //  требуется создавать новый экземпляр вьюшки по команде (при начале гонки). Каждый экземпляр -
        //  отдельный рисунок трека. При этом можно реализовать раздельное управление треками (удаление, перекраска, и т.д.)
    }

    public DrawView getDrawView() {
        Log.i(PROJECT_LOG_TAG, "sending drawView from TrackPainter");
        return drawView;
    }

    public void beginNewTrack (Location location) {
        startPointLatitude = location.getLatitude();
        startPointLongitude = location.getLongitude();
    }

    public void addLocationIntoTrack (Location location) {
        int actualPointX = calculateLocalX(location);
        int actualPointY = calculateLocalY(location);

        if (lastLocation == null) {
            beginNewTrack(location);
            drawView.setStartPoint(actualPointX, actualPointY);
            lastLocation = location;
        } else {
            drawView.drawNextPoint(actualPointX, actualPointY);
        }
    }

    private int calculateLocalX (Location location) {
        double coordinateDifferent = location.getLongitude() - startPointLongitude;
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
