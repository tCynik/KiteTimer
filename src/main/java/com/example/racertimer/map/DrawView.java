package com.example.racertimer.map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class DrawView extends View {
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

    public void setStartCoordinates(int prevCoordinateX, int prevCoordinateY) {
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

