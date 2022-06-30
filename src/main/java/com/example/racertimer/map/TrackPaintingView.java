package com.example.racertimer.map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.location.Location;
import android.util.Log;
import android.view.View;

public class TrackPaintingView extends View {
    private final static String PROJECT_LOG_TAG = "racer_timer_draw";

    MapManager mapManager;

    private String trackName;

    private TrackGridCalculator trackGridCalculator;

    private Paint paint;
    private Path path;

    float centerOfViewX, centerOfViewY, windowCenterX, windowCenterY;

    private float lastCoordinateX, lastCoordinateY;

    private float currentCoordinateX, currentCoordinateY;
    private float coordinateXToDraw, coordinateYToDraw;

    public TrackPaintingView(Context context, TrackGridCalculator trackGridCalculator) {
        super (context);
        Log.i(PROJECT_LOG_TAG, "draw view instance was created");
        this.trackGridCalculator = trackGridCalculator;

        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(20);
        paint.setStyle(Paint.Style.STROKE);

        path = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(path, paint);
    }

    public void drawNextSegmentByLocation(Location location) {
        calculateCoordinates(location);
        drawLine();
    }

    public void calculateCoordinates(Location location) {
        currentCoordinateX = trackGridCalculator.calculateLocalX(location); // нынешние координаты в системе координат лайаута
        currentCoordinateY = trackGridCalculator.calculateLocalY(location);

        if (centerOfViewX == 0) mapManager.setScreenCenterToView(this);
        coordinateXToDraw = centerOfViewX + currentCoordinateX;
        coordinateYToDraw = centerOfViewY + currentCoordinateY;
    }

    private void drawLine () {
        // TODO: пока оставлю так, что границы динамически не меняем а у контейнера вьюшки вручную
        //  установлены огромные границы. В будущем потребуется их динамическое определение (при дальних поездках, марафонах, итд)

        path.lineTo(coordinateXToDraw, coordinateYToDraw); // пока все вычисления для теста отрисовки
        invalidate();
        path.moveTo(coordinateXToDraw, coordinateYToDraw);

        lastCoordinateX = currentCoordinateX;
        lastCoordinateY = currentCoordinateY;
    }

    public void setScreenCenterCoordinates (float viewCenterX, float viewCenterY, float windowCenterX, float windowCenterY) {
        this.centerOfViewX = viewCenterX;
        this.centerOfViewY = viewCenterY;
        this.windowCenterX = windowCenterX;
        this.windowCenterY = windowCenterY;

        path.moveTo(viewCenterX, viewCenterY); //TODO: wtf? не этот метод должен двигать экран
    }

    // TODO: надо разбираться с расчетом координат. Сейчас бардак: двухстадийный расчет отдельно для трека, отдельно для смещения экрана.

    // TODO: когда кончается вьюшка, она не хочет менять размер! Придется составлять трек из массива вьюшек?


    //TODO: пока начало рисования в нулевой точке (грубо, где создается карта) При рисовании со старта норм,
    // но если запускаем не сразу (через стартовую процедуру), будем связыватсья с 0? Или при импорте трека для прорисовки
    // нужно в первую точку линию не рисовать! И при этом обойти частный случае если посреди трека появятся координаты 0, 0

    public void setMapManager(MapManager mapManager) {
        this.mapManager = mapManager;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    // TODO: 1. в рамках обьекта храним в массиве все точки трека (как location)
//2. в случае выхода координат точки за -0 меняем стартовую точку, и от нее перестраиваем весь трек
//
//ОДНАКО в этом случае получится странная история со смещением экрана. в идеале свой экран должна смещать сама вьюшка
//надо разбиратсья с делегирвоанием
}

