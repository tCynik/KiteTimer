package com.example.racertimer.multimedia;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

import com.example.racertimer.R;

/** класс для озвучивания событий
 * приоритетность звуков priority:
 * PRIORITY_SYSTEM = 1 - системные звуки
 * PRIORITY_BEEP = 2 - звуки индикации положения VMG
 * PRIORITY_TIMER = 3 - озвучка таймера */

public class Voiceover {
    private final static String PROJECT_LOG_TAG = "racer_timer";

    private static final int PRIORITY_SYSTEM = 1; // системные звуки
    private static final int PRIORITY_BEEP = 2; // звуки индикации положения VMG
    private static final int PRIORITY_TIMER = 3; // озвучка таймера

    public static int SOUND_ASSET_START = 1;
    public static int SOUND_ASSET_ONE   = 2;
    public static int SOUND_ASSET_TWO = 3;
    public static int SOUND_ASSET_THREE = 4;
    public static int SOUND_ASSET_FOUR = 5;
    public static int SOUND_ASSET_FIVE = 6;
    public static int SOUND_ASSET_SIX = 7;
    public static int SOUND_ASSET_SEVEN = 8;
    public static int SOUND_ASSET_EIGHT = 9;
    public static int SOUND_ASSET_NINE = 10;
    public static int SOUND_ASSET_TEN = 11;
    public static int SOUND_ASSET_FIFTEEN = 12;
    public static int SOUND_ASSET_TWENTY = 13;
    public static int SOUND_ASSET_THIRTY = 14;
    public static int SOUND_ASSET_FORTY = 15;
    public static int SOUND_ASSET_FIFTY = 16;
    public static int SOUND_ASSET_ONE_MINUTE = 17;
    public static int SOUND_ASSET_ONE_MINUTE_READY = 18;
    public static int SOUND_ASSET_TWO_MINUTE = 19;
    public static int SOUND_ASSET_TWO_MINUTE_READY = 20;
    public static int SOUND_ASSET_PAUSE = 21;
    public static int SOUND_ASSET_BEEP = 22;
    public static int SOUND_ASSET_BEEP_PATCH = 23;

    public SoundPool soundPool;

    private Context context;
    public boolean vmgIsMuted = false;

    int repeatSoundId = 0; // айди текущего повторяющегося звука

    public Voiceover(Context context) {

        /** блок инициализации SoundPool */
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANT)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .build();

        /** блок подгрузки ассетов озвучки (загружаем в порядке обсявления переменных SOUND_ASSET_...) */
        soundPool.load(context, R.raw.start_cow_bell, PRIORITY_TIMER); // 1
        soundPool.load(context, R.raw.eng_one, PRIORITY_TIMER); // 2
        soundPool.load(context, R.raw.eng_two, PRIORITY_TIMER); // 3
        soundPool.load(context, R.raw.eng_three, PRIORITY_TIMER); // 4
        soundPool.load(context, R.raw.eng_four, PRIORITY_TIMER); // 5
        soundPool.load(context, R.raw.eng_five, PRIORITY_TIMER); // 6
        soundPool.load(context, R.raw.eng_six, PRIORITY_TIMER); // 7
        soundPool.load(context, R.raw.eng_seven, PRIORITY_TIMER); // 8
        soundPool.load(context, R.raw.eng_eight, PRIORITY_TIMER); // 9
        soundPool.load(context, R.raw.eng_nine, PRIORITY_TIMER); // 10
        soundPool.load(context, R.raw.eng_ten, PRIORITY_TIMER); // 11
        soundPool.load(context, R.raw.eng_ten, PRIORITY_TIMER); // 12 - это 15, пока нет озвучки
        soundPool.load(context, R.raw.eng_twenty, PRIORITY_TIMER); // 13
        soundPool.load(context, R.raw.eng_threety, PRIORITY_TIMER); // 14
        soundPool.load(context, R.raw.eng_fourty, PRIORITY_TIMER); // 15
        soundPool.load(context, R.raw.eng_fivety, PRIORITY_TIMER); // 16
        soundPool.load(context, R.raw.eng_one_minute, PRIORITY_TIMER); // 17
        soundPool.load(context, R.raw.eng_one_minute_ready, PRIORITY_TIMER); // 18
        soundPool.load(context, R.raw.eng_two_minutes, PRIORITY_TIMER); // 19
        soundPool.load(context, R.raw.eng_two_minutes_ready, PRIORITY_TIMER); // 20
        soundPool.load(context, R.raw.eng_pause, PRIORITY_TIMER); // 21

        soundPool.load(context, R.raw.beep, PRIORITY_BEEP); // 22
        soundPool.load(context, R.raw.patch_beep, PRIORITY_BEEP +1); // 23

        this.context = context;
    }

    public void playSingleTimerSound (int soundAssertId) { // проигрывание одиночных звуков - таймер
        try {
            soundPool.play(soundAssertId, 1, 1, PRIORITY_TIMER, 0, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playSingleSystemSound (int soundAssertId) { // проигрывание одиночных звуков - система
        try {
            soundPool.play(soundAssertId, 1, 1, PRIORITY_SYSTEM, 0, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playRepeatSound (int percentVMG) { // проигрывание циклических звуков - пищалка ВМГ
        if (!vmgIsMuted) { // если небыло команды об отключении звука
            if (repeatSoundId !=0 ) { // если уже что-то играем, то сначала останавливаем звук
                // одиночный патч для маскировки прерывания звука
                try {
                    soundPool.play(SOUND_ASSET_BEEP_PATCH, 1, 1, PRIORITY_BEEP+1, 0, calculateRateFromPercent(percentVMG));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                stopRepeatSound(); // останавливаем воспроизведение
//                soundPool.stop(repeatSoundId); // останавливаем ранее воспроизводимое пиканьше
//                repeatSoundId = 0; // обнуляем айдишник воспроизведения
            }
            // запускаем непосредственно проигрывание
            startPlayingRepeatSound(percentVMG);
        }
    }

    private void startPlayingRepeatSound (int percentVMG) {
        float rate = calculateRateFromPercent(percentVMG); // высчитываем скорость воспроизведения
        repeatSoundId = soundPool.play(SOUND_ASSET_BEEP, 1, 1, PRIORITY_BEEP, 100, rate);
        while (repeatSoundId == 0) // повторяем запуск звука пока он не запустится
            repeatSoundId = soundPool.play(SOUND_ASSET_BEEP, 1, 1, PRIORITY_BEEP, 100, rate);
//        Log.i("racer_timer", " beeping started, id = " + repeatSoundId);
    }

    public void stopRepeatSound () { // остановка пищалки
        if (repeatSoundId !=0)
            try {
                soundPool.stop(repeatSoundId); // останавливаем звук с текущим айдишником
            } catch (Exception e) {
                e.printStackTrace();
            }
        repeatSoundId = 0; // обнуляем айдишник
//        Log.i("racer_timer", " beeping stopped ");
    }

    public static float calculateRateFromPercent (int percentVMG) { // считаем скорость пищания в зависимости от процента
        float rate = (float) ((float)((percentVMG * 1.5) / 100) + 0.5); // диапазон скоростей от 0,5 до 2
        return rate;
    }
}
