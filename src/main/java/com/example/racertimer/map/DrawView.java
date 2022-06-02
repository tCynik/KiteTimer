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

    MapManager mapManager;

    private TrackGridCalculator trackGridCalculator;

    private Paint paint;
    private Path path;

    float centerOfViewX, centerOfViewY, windowCenterX, windowCenterY;

    private float lastCoordinateX, lastCoordinateY;

    private float currentCoordinateX, currentCoordinateY;
    private float coordinateXToDraw, coordinateYToDraw, viewShiftX, viewShiftY;

    private float borderXShift = 0; // сдвиг для динамического расширения вьюшки при необходимости
    private float borderYShift = 0;
    private int boarderShiftStep;

    private double trackStartLongitude; // точка начала трека принимается как начало отсчета карты...
    private double trackStartLatitude;  //      т.е. Х=0+сдвиг У=0+сдвиг в локальной системе отсчета

    //private boolean screenCenterOnLocation = true;

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
        //makeBorderShiftStep();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(path, paint);
    }

    public void onLocationChanged (Location location) {
        calculateCoordinates(location);
        drawLine();
        recordWaypoint(location);
        //if (screenCenterOnLocation) moveScreenToCoordinate();
    }

    public void calculateCoordinates(Location location) {
        currentCoordinateX = trackGridCalculator.calculateLocalX(location); // нынешние координаты в системе координат лайаута
        currentCoordinateY = trackGridCalculator.calculateLocalY(location);

        if (centerOfViewX == 0) mapManager.setScreenCenterToView();
        coordinateXToDraw = centerOfViewX + currentCoordinateX;
        coordinateYToDraw = centerOfViewY + currentCoordinateY;

        viewShiftX = (coordinateXToDraw + 400) * -1; //(centerOfViewX + currentCoordinateY - windowCenterX) * -1;
        viewShiftY = (coordinateYToDraw + 400) * -1;//(centerOfViewY + currentCoordinateY - windowCenterY) * -1;

    }

    private void drawLine () {
        //checkViewBoarders();
        // TODO: пока оставлю так, что границы динамически не меняем а у контейнера вьюшки вручную
        //  установлены огромные границы. В будущем потребуется их динамическое определение (при дальних поездках, марафонах, итд)
        //Log.i(PROJECT_LOG_TAG, "invalidating point: from "+ lastCoordinateX +" : "+ lastCoordinateY +" to "+currentCoordinateX + " : "+currentCoordinateY);

        path.lineTo(coordinateXToDraw, coordinateYToDraw); // пока все вычисления для теста отрисовки
        invalidate();
        path.moveTo(coordinateXToDraw, coordinateYToDraw);

        lastCoordinateX = currentCoordinateX;
        lastCoordinateY = currentCoordinateY;
    }

    private void recordWaypoint(Location location) {
        // TODO: recording the location into array
    }

//    private void moveScreenToCoordinate() {
//        trackPainterOnMap.setScreenToCoordinates(viewShiftX, viewShiftY);
//
////        if (boarderShiftStep == 0)
////            boarderShiftStep = this.getWidth() / 2;
//    }

    public void setScreenCenterCoordinates (float viewCenterX, float viewCenterY, float windowCenterX, float windowCenterY) {
        this.centerOfViewX = viewCenterX;
        this.centerOfViewY = viewCenterY;
        this.windowCenterX = windowCenterX;
        this.windowCenterY = windowCenterY;

        path.moveTo(viewCenterX, viewCenterY); //TODO: wtf? не этот метод должен двигать экран
    }

//    private void checkViewBoarders() {
//        Log.i(PROJECT_LOG_TAG, "!!! current X = " + coordinateXToDraw+", width = " +this.getWidth() );
//
//        if (coordinateXToDraw > this.getWidth()) {
//            Log.i(PROJECT_LOG_TAG, "expending X board " );
//            expendBoarderX();
//        }
//        if (currentCoordinateY > this.getHeight()) expendBorderY();
//    }

    private void expendBoarderX() {
        this.setMinimumWidth(this.getWidth() + boarderShiftStep);
        Log.i(PROJECT_LOG_TAG, "boardX was expended to "+ (this.getWidth() + boarderShiftStep));
        Log.i(PROJECT_LOG_TAG, "width now = "+ this.getWidth());
    }

    private void expendBorderY() {}

    // TODO: надо разбираться с расчетом координат. Сейчас бардак: двухстадийный расчет отдельно для трека, отдельно для смещения экрана.

    // TODO: когда кончается вьюшка, она не хочет менять размер! Придется составлять трек из массива вьюшек?


    //TODO: пока начало рисования в нулевой точке (грубо, где создается карта) При рисовании со старта норм,
    // но если запускаем не сразу (через стартовую процедуру), будем связыватсья с 0? Или при импорте трека для прорисовки
    // нужно в первую точку линию не рисовать! И при этом обойти частный случае если посреди трека появятся координаты 0, 0

    public double getTrackStartLongitude() {
        return trackStartLongitude;
    }

    public double getTrackStartLatitude() {
        return trackStartLatitude;
    }

    public void setTrackPainterOnMap(MapManager mapManager) {
        this.mapManager = mapManager;
    }

//    нужно сделать стартовую привязочную точку
// TODO: 1. в рамках обьекта храним в массиве все точки трека (как location)
//2. в случае выхода координат точки за -0 меняем стартовую точку, и от нее перестраиваем весь трек
//
//ОДНАКО в этом случае получится странная история со смещением экрана. в идеале свой экран должна смещать сама вьюшка
//надо разбиратсья с делегирвоанием
}

