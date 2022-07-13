package com.example.racertimer.multimedia;

import android.content.Context;
import android.util.Log;

import com.example.racertimer.R;

public class BeepSounds extends ParentVoiceover {
    private static final int PRIORITY_BEEP = 2; // звуки индикации положения VMG

    public static int SOUND_ASSET_BEEP =       1;
    public static int SOUND_ASSET_BEEP_PATCH = 2;

    public BeepSounds(Context context) {
        super(context);
        soundPool.load(context, R.raw.beep, PRIORITY_BEEP); // 22
        soundPool.load(context, R.raw.patch_beep, (PRIORITY_BEEP +1)); // 23

    }

    public void playRepeatSound (int percentVMG) { // проигрывание циклических звуков - пищалка ВМГ
        Log.i("racer_timer_tools_fragment", " called playRepeatSound with percent = "+percentVMG+", mute = "+vmgIsMuted);

        if (repeatSoundId !=0 ) { // если уже что-то играем, то сначала останавливаем звук
            try { // одиночный патч для маскировки прерывания звука
                soundPool.play(SOUND_ASSET_BEEP_PATCH, 1, 1, PRIORITY_BEEP+1, 0, calculateRateFromPercent(percentVMG));
            } catch (Exception e) {
                e.printStackTrace();
            }
            stopRepeatSound(); // останавливаем воспроизведение ранее запущенной пищалки
        }
        if (!vmgIsMuted) startPlayingRepeatSound(percentVMG); // запускаем непосредственно проигрывание
    }

    private void startPlayingRepeatSound (int percentVMG) {
        float rate = calculateRateFromPercent(percentVMG); // высчитываем скорость воспроизведения
        Log.i("racer_timer_tools_fragment", " start playing with rate ="+rate);
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
        Log.i("racer_timer", " beeping stopped ");
    }

    public static float calculateRateFromPercent (int percentVMG) { // считаем скорость пищания в зависимости от процента
        float rate = (float) ((float)((percentVMG * 1.5) / 100) + 0.5); // диапазон скоростей от 0,5 до 2
        return rate;
    }

    public void voiceoverIsBeingMuted () {
        vmgIsMuted = true;
        stopRepeatSound();
    }
}
