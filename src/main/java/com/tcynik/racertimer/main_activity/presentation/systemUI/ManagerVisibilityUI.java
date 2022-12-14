package com.tcynik.racertimer.main_activity.presentation.systemUI;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.racertimer.R;
import com.tcynik.racertimer.main_activity.MainActivity;

public class ManagerVisibilityUI {
    private MainActivity context;
    private TimeoutEndingInterface timeoutEndingInterface = new TimeoutEndingInterface() {
        @Override
        public void onTimeoutEnded() {
            setBarInvisible();
        }
    };
    private TimeoutInterface timeoutManager;// = new TimeOutManager(context, timeoutEndingInterface);

    public ManagerVisibilityUI(MainActivity context) {
        this.context = context;
        Log.i("bugfix: managerVisibility", "manager was created");
        timeoutManager = new TimeOutManager(context, timeoutEndingInterface);
        setBarInvisible();
        TextView racingTimerTV = context.findViewById(R.id.racing_timer);
        context.getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                hideBarAfterTimeout();
            }
        });
    }

    public void hideBarAfterTimeout() {
        if (isBarVisible()) {
            Log.i("bugfix: managerVisibility", "running timeout");
            timeoutManager.sartTimeoutToHide();
        }
    }

    private boolean isBarVisible() {
        int visibility = context.getWindow().getDecorView().getVisibility();
        Log.i("bugfix: managerVisibility", "checking the bar visibility: "+visibility);
        return (visibility == 0);//View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    }

    private void setBarInvisible() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                TextView racingTimerTV = context.findViewById(R.id.racing_timer);
                context.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            }
        };
        context.runOnUiThread(runnable);
    }
}
