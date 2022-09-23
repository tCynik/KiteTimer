package com.example.racertimer.multimedia;

import android.content.Context;

/** This is the class for voiceover the app system events.
 * It is empty yet because the app's events are not has any voiceover.
 * It might be be realized later
 */

public class SystemSounds extends VoiceoverMain {
    private static final int PRIORITY_SYSTEM = 1; // системные звуки

//    public static int SOUND_ASSET_START = 1;
//    public static int SOUND_ASSET_PAUSE = 2;

    public SystemSounds(Context context) {
        super();
//        soundPool.load(context, R.raw.start_cow_bell, PRIORITY_SYSTEM); // 1
//        soundPool.load(context, R.raw.eng_pause, PRIORITY_SYSTEM); // 21

    }
}
