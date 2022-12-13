package com.example.racertimer.main_activity.presentation.interfaces;

public interface StatusUiUpdater {
    void onStatusChecked(boolean status);

    void updateUIModuleStatus(String moduleName, boolean isItReady);
}
