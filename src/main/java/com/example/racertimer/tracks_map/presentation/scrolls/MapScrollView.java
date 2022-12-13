package com.example.racertimer.tracks_map.presentation.scrolls;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class MapScrollView extends ScrollView {
    private int origX, origY;
    private final float THRESHOLD = 60;

    public MapScrollView(Context context) {
        super(context);
    }

    public MapScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MapScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MapScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
//            origX = (int) ev.getX();
//            origY = (int) ev.getY();
//        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
//            float deltaX = Math.abs(ev.getX() - origX);
//            float deltaY = Math.abs(ev.getY() - origY);
//            return deltaX >= THRESHOLD || deltaY >= THRESHOLD;
//        }
//        onInterceptTouchEvent(ev);
//        return false;

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
//        super.onTouchEvent(ev);
//        return true;
        return super.onTouchEvent(ev);
    }
}
