package com.example.racertimer.multimedia;

import android.content.Context;

import com.example.racertimer.R;

public class TimerVoiceover extends VoiceoverMain {
    private static final int PRIORITY_TIMER = 3; // озвучка таймера

    public static int SOUND_ASSET_ONE   =             1;
    public static int SOUND_ASSET_TWO =               2;
    public static int SOUND_ASSET_THREE =             3;
    public static int SOUND_ASSET_FOUR =              4;
    public static int SOUND_ASSET_FIVE =              5;
    public static int SOUND_ASSET_SIX =               6;
    public static int SOUND_ASSET_SEVEN =             7;
    public static int SOUND_ASSET_EIGHT =             8;
    public static int SOUND_ASSET_NINE =              9;
    public static int SOUND_ASSET_TEN =              10;
    public static int SOUND_ASSET_FIFTEEN =          11;
    public static int SOUND_ASSET_TWENTY =           12;
    public static int SOUND_ASSET_THIRTY =           13;
    public static int SOUND_ASSET_FORTY =            14;
    public static int SOUND_ASSET_FIFTY =            15;
    public static int SOUND_ASSET_ONE_MINUTE =       16;
    public static int SOUND_ASSET_ONE_MINUTE_READY = 17;
    public static int SOUND_ASSET_TWO_MINUTE =       18;
    public static int SOUND_ASSET_TWO_MINUTE_READY = 19;
    public static int SOUND_ASSET_START =            20;
    public static int SOUND_ASSET_PAUSE =            21;


    public TimerVoiceover(Context context) {
        super();

        soundPool.load(context, R.raw.eng_one, PRIORITY_TIMER); // 1 - порядковый номер подгурзки ассета
        soundPool.load(context, R.raw.eng_two, PRIORITY_TIMER); // 2
        soundPool.load(context, R.raw.eng_three, PRIORITY_TIMER); // 3
        soundPool.load(context, R.raw.eng_four, PRIORITY_TIMER); // 4
        soundPool.load(context, R.raw.eng_five, PRIORITY_TIMER); // 5
        soundPool.load(context, R.raw.eng_six, PRIORITY_TIMER); // 6
        soundPool.load(context, R.raw.eng_seven, PRIORITY_TIMER); // 7
        soundPool.load(context, R.raw.eng_eight, PRIORITY_TIMER); // 8
        soundPool.load(context, R.raw.eng_nine, PRIORITY_TIMER); // 9
        soundPool.load(context, R.raw.eng_ten, PRIORITY_TIMER); // 10
        soundPool.load(context, R.raw.eng_ten, PRIORITY_TIMER); // 11 - это место под 15 сек, пока нет озвучки
        soundPool.load(context, R.raw.eng_twenty, PRIORITY_TIMER); // 12
        soundPool.load(context, R.raw.eng_threety, PRIORITY_TIMER); // 13
        soundPool.load(context, R.raw.eng_fourty, PRIORITY_TIMER); // 14
        soundPool.load(context, R.raw.eng_fivety, PRIORITY_TIMER); // 15
        soundPool.load(context, R.raw.eng_one_minute, PRIORITY_TIMER); // 16
        soundPool.load(context, R.raw.eng_one_minute_ready, PRIORITY_TIMER); // 17
        soundPool.load(context, R.raw.eng_two_minutes, PRIORITY_TIMER); // 18
        soundPool.load(context, R.raw.eng_two_minutes_ready, PRIORITY_TIMER); // 19
        soundPool.load(context, R.raw.start_cow_bell, PRIORITY_TIMER); // 20
        soundPool.load(context, R.raw.eng_pause, PRIORITY_TIMER); // 21
    }
}
