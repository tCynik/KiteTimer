package com.example.racertimer.map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

public class TrackPainterOnMap extends Activity {
    private final static String PROJECT_LOG_TAG = "racer_timer_painter";

    private DrawView drawView;
    private int trackAccuracy = 5; // точность прорисовки трека = 5й знак после запятой в координатах

    private Location lastLocation = null;
    private double startPointLongitude; // точка начала трека принимается как начало отсчета карты...
    private double startPointLatitude;  // т.е. Х=0 У=0 в локальной системе отсчета

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        drawView = new DrawView(this);
        setContentView(drawView);
    }

    class DrawView extends View {

        Paint paint;

        float prevCoordinateX;
        float prevCoordinateY;

        float currentCoordinateX;
        float currentCoordinateY;

        public DrawView (Context context) {
            super (context);
            paint = new Paint();
            prevCoordinateX = 0;
            prevCoordinateY = 0; // начинаем рисовать с нуля (центр экрана)
        }

        @Override
        protected void onDraw(Canvas canvas) {
            //canvas.drawColor(Color.WHITE);

            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(5);

            canvas.drawLine (prevCoordinateX, currentCoordinateY, currentCoordinateX, currentCoordinateY, paint);

        }

        public void setStartPoint (int prevCoordinateX, int prevCoordinateY) {
            this.prevCoordinateX = prevCoordinateX;
            this.prevCoordinateY = prevCoordinateY;
            //TODO: пока начало рисования в нулевой точке (грубо, где создается карта) При рисовании со старта норм,
            // но если запускаем не сразу (через стартовую процедуру), будем связыватсья с 0? Или при импорте трека для прорисовки
            // нужно в первую точку линию не рисовать! И при этом обойти частный случае если посреди трека появятся координаты 0, 0
        }

        public void drawNextPoint (int currentCoordinateX, int currentCoordinateY) {
            this.currentCoordinateX = currentCoordinateX;
            this.currentCoordinateY = currentCoordinateY;

            invalidate();

            prevCoordinateX = currentCoordinateX;
            prevCoordinateY = currentCoordinateY;
        }
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

// TODO: найти привязку канвы или окружения к конкретной вьюшке; залогировать код и дальше рестить всё