package com.example.racertimer.trackMap;

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

    private float currentCoordinateX, currentCoordinateY;
    private float coordinateXToDraw, coordinateYToDraw;

    public TrackPaintingView(
            Context context,
            MapManager mapManager,
            TrackGridCalculator trackGridCalculator,
            Location location
    ) {
        super (context);
        Log.i(PROJECT_LOG_TAG, "draw view instance was created");
        this.trackGridCalculator = trackGridCalculator;
        this.mapManager = mapManager;
        paint = new Paint();
        //paint.setStrokeWidth(20);
        paint.setStyle(Paint.Style.STROKE);

        path = new Path();

        setToStartPosition(location);
    }

    protected void setPathAttributes (int color, int width) {
        paint.setColor(color);
        paint.setStrokeWidth(width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(path, paint);
    }

    private void setToStartPosition(Location location){
        calculateCoordinates(location);
        path.moveTo(coordinateXToDraw, coordinateYToDraw);
    }

    public void drawNextSegmentByLocation(Location location) {
        calculateCoordinates(location);
        drawLine();
    }

    public void calculateCoordinates(Location location) {
        if (trackGridCalculator == null) {
            trackGridCalculator = mapManager.trackGridCalculator;
        }
        if (centerOfViewX == 0) mapManager.setScreenCenterToPaintingView(this);

        currentCoordinateX = trackGridCalculator.calculateLocalX(location); // нынешние координаты в системе координат лайаута
        currentCoordinateY = trackGridCalculator.calculateLocalY(location);

        coordinateXToDraw = centerOfViewX + currentCoordinateX;
        coordinateYToDraw = centerOfViewY + currentCoordinateY;
    }

    private void drawLine () {
        // TODO: пока оставлю так, что границы динамически не меняем а у контейнера вьюшки вручную
        //  установлены огромные границы. В будущем потребуется их динамическое определение (при дальних поездках, марафонах, итд)

        path.lineTo(coordinateXToDraw, coordinateYToDraw); // пока все вычисления для теста отрисовки
        path.moveTo(coordinateXToDraw, coordinateYToDraw);
        invalidate();
    }

    public void setScreenCenterCoordinates (float viewCenterX, float viewCenterY, float windowCenterX, float windowCenterY) {
        this.centerOfViewX = viewCenterX;
        this.centerOfViewY = viewCenterY;
        this.windowCenterX = windowCenterX;
        this.windowCenterY = windowCenterY;
    }

    // TODO: когда кончается вьюшка, она не хочет менять размер! Придется составлять трек из массива вьюшек?

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

class CurrentTrackLine extends TrackPaintingView {
    int trackColor = Color.WHITE;//R.color.current_track_line;
    int trackLineWidth = 20;

    public CurrentTrackLine(Context context, MapManager mapManager, TrackGridCalculator trackGridCalculator, Location location) {
        super(context, mapManager, trackGridCalculator, location);
        super.setPathAttributes(trackColor, trackLineWidth);
    }
}

class LoadedTrackLine extends TrackPaintingView {
    int trackColor = Color.GREEN;//R.color.loaded_track_line;
    int trackLineWidth = 10;

    public LoadedTrackLine(Context context, MapManager mapManager, TrackGridCalculator trackGridCalculator, Location location) {
        super(context, mapManager, trackGridCalculator, location);
        super.setPathAttributes(trackColor, trackLineWidth);
    }
}

class DutyTrackLine extends TrackPaintingView {
    int trackColor = Color.GRAY;//R.color.duty_track_line;
    int trackLineWidth = 10;

    public DutyTrackLine(Context context, MapManager mapManager, TrackGridCalculator trackGridCalculator, Location location) {
        super(context, mapManager, trackGridCalculator, location);
        super.setPathAttributes(trackColor, trackLineWidth);
    }
}
