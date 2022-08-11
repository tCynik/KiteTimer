package com.example.racertimer;

public interface StatusUiUpdater {
    void onStatusChecked(boolean status);

    void updateUIModuleStatus(String moduleName, boolean isItReady);
}
