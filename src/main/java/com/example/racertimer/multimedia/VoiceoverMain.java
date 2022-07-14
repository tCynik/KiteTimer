package com.example.racertimer.multimedia;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

/** класс для озвучивания событий
 * приоритетность звуков priority:
 * PRIORITY_SYSTEM = 1 - системные звуки
 * PRIORITY_BEEP = 2 - звуки индикации положения VMG
 * PRIORITY_TIMER = 3 - озвучка таймера */

public class VoiceoverMain {
    private final static String PROJECT_LOG_TAG = "racer_timer";

    private static final int PRIORITY_SYSTEM = 1; // системные звуки
    private static final int PRIORITY_BEEP = 2; // звуки индикации положения VMG
    private static final int PRIORITY_TIMER = 3; // озвучка таймера
    public SoundPool soundPool;

    private Context context;
    public boolean vmgIsMuted = false;

    int repeatSoundId = 0; // айди текущего повторяющегося звука

    public VoiceoverMain(Context context) {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANT)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .build();
        this.context = context;
    }

    public void playSound(int soundAssertId){
        try {
            soundPool.play(soundAssertId, 1, 1, PRIORITY_TIMER, 0, 1);
            //Log.i("bugfix", "voiceover: playing sound # "+ soundAssertId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}