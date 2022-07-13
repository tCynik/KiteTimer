package com.example.racertimer.multimedia;

import android.content.Context;

import com.example.racertimer.R;

public class SystemSounds extends ParentVoiceover {
    private static final int PRIORITY_SYSTEM = 1; // системные звуки

    public static int SOUND_ASSET_START = 1;
    public static int SOUND_ASSET_PAUSE = 2;

    public SystemSounds(Context context) {
        super(context);
        soundPool.load(context, R.raw.start_cow_bell, PRIORITY_SYSTEM); // 1
        soundPool.load(context, R.raw.eng_pause, PRIORITY_SYSTEM); // 21

    }
}
