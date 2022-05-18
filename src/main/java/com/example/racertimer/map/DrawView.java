package com.example.racertimer.map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.View;

public class DrawView extends View {
    private final static String PROJECT_LOG_TAG = "racer_timer_draw";

    Paint paint;

    Path path;

    float prevCoordinateX;
    float prevCoordinateY;

    float currentCoordinateX;
    float currentCoordinateY;

    public DrawView (Context context) {
        super (context);
        Log.i(PROJECT_LOG_TAG, "draw view instance was created");

        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(20);
        paint.setStyle(Paint.Style.STROKE);

        path = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.i(PROJECT_LOG_TAG, "drawing in drawViews's onDraw");

//        path.lineTo(300, 300);
        path.lineTo((currentCoordinateX*10+200), (currentCoordinateY*10+200)); // пока все вычисления для теста отрисовки
        canvas.drawPath(path, paint);
        path.moveTo(currentCoordinateX, currentCoordinateY);

        Log.i(PROJECT_LOG_TAG, "drawing line: from "+prevCoordinateX + " : "+prevCoordinateY+ " to " + currentCoordinateX+ " : "+ currentCoordinateY);
    }

    public void drawNextLine(int currentCoordinateX, int currentCoordinateY) {
        this.currentCoordinateX = currentCoordinateX;
        this.currentCoordinateY = currentCoordinateY;

        Log.i(PROJECT_LOG_TAG, "invalidating point: from "+prevCoordinateX+" : "+prevCoordinateY+" to "+currentCoordinateX + " : "+currentCoordinateY);

        invalidate();
//
//        prevCoordinateX = currentCoordinateX;
//        prevCoordinateY = currentCoordinateY;
    }

    public void setStartCoordinates(int prevCoordinateX, int prevCoordinateY) {
        this.prevCoordinateX = prevCoordinateX;
        this.prevCoordinateY = prevCoordinateY;
        //TODO: пока начало рисования в нулевой точке (грубо, где создается карта) При рисовании со старта норм,
        // но если запускаем не сразу (через стартовую процедуру), будем связыватсья с 0? Или при импорте трека для прорисовки
        // нужно в первую точку линию не рисовать! И при этом обойти частный случае если посреди трека появятся координаты 0, 0
    }


}

