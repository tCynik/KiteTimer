package com.example.racertimer.map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.location.Location;
import android.util.Log;
import android.view.View;

public class DrawView extends View {
    private final static String PROJECT_LOG_TAG = "racer_timer_draw";

    TrackPainterOnMap trackPainterOnMap;

    private TrackGridCalculator trackGridCalculator;

    private Paint paint;
    private Path path;

    float screenCenterX;
    float screenCenterY;

    float prevCoordinateX;
    float prevCoordinateY;

    float currentCoordinateX;
    float currentCoordinateY;

    private double trackStartLongitude; // точка начала трека принимается как начало отсчета карты...
    private double trackStartLatitude;  //      т.е. Х=0 У=0 в локальной системе отсчета

    public DrawView (Context context, Location location) {
        super (context);
        Log.i(PROJECT_LOG_TAG, "draw view instance was created");
        trackGridCalculator = new TrackGridCalculator(location);

        trackStartLatitude = location.getLatitude();
        trackStartLongitude = location.getLongitude();

        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(20);
        paint.setStyle(Paint.Style.STROKE);

        path = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.i(PROJECT_LOG_TAG, "drawing in drawViews's onDraw");
        currentCoordinateX = screenCenterX + currentCoordinateX*10;
        currentCoordinateY = screenCenterY + currentCoordinateY*10;
        path.lineTo(currentCoordinateX, currentCoordinateY); // пока все вычисления для теста отрисовки
        canvas.drawPath(path, paint);
        path.moveTo(currentCoordinateX, currentCoordinateY);

        Log.i(PROJECT_LOG_TAG, "drawing line: from "+prevCoordinateX + " : "+prevCoordinateY+ " to " + currentCoordinateX+ " : "+ currentCoordinateY);
    }

    public void setScreenCenterCoordinates (float screenCenterX, float screenCenterY) {
        this.screenCenterX = screenCenterX;
        this.screenCenterY = screenCenterY;
        path.moveTo(screenCenterX, screenCenterY);
    }

    public void onLocationChanged (Location location) {
        this.currentCoordinateX = trackGridCalculator.calculateLocalX(location); //calculateLocalX(location); // нынешние координаты в системе координат лайаута
        this.currentCoordinateY = trackGridCalculator.calculateLocalY(location); //calculateLocalY(location);

        Log.i(PROJECT_LOG_TAG, "invalidating point: from "+prevCoordinateX+" : "+prevCoordinateY+" to "+currentCoordinateX + " : "+currentCoordinateY);
        invalidate();

        setScreenCenter();
    }

    private void setScreenCenter () {
        trackPainterOnMap.setScreenToCoordinates(currentCoordinateX*10, currentCoordinateY*10);
        // TODO: надо разобраться, где выставлять центр координат - здесь или в трэк пэйнтере
        //  скорей здесь, т.к. при расширении вьюшки поменяется и центр

    }

    //TODO: пока начало рисования в нулевой точке (грубо, где создается карта) При рисовании со старта норм,
    // но если запускаем не сразу (через стартовую процедуру), будем связыватсья с 0? Или при импорте трека для прорисовки
    // нужно в первую точку линию не рисовать! И при этом обойти частный случае если посреди трека появятся координаты 0, 0

    public double getTrackStartLongitude() {
        return trackStartLongitude;
    }

    public double getTrackStartLatitude() {
        return trackStartLatitude;
    }

    public void setTrackPainterOnMap(TrackPainterOnMap trackPainterOnMap) {
        this.trackPainterOnMap = trackPainterOnMap;
        //trackPainterOnMap.setScreenToCenter();
    }

//    нужно сделать стартовую привязочную точку
// TODO: 1. в рамках обьекта храним в массиве все точки трека (как location)
//2. в случае выхода координат точки за -0 меняем стартовую точку, и от нее перестраиваем весь трек
//
//ОДНАКО в этом случае получится странная история со смещением экрана. в идеале свой экран должна смещать сама вьюшка
//надо разбиратсья с делегирвоанием
}

