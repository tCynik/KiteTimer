package com.example.racertimer.multimedia;

import android.content.Context;
import android.media.MediaPlayer;

import com.example.racertimer.R;

public class Voiceover {
    public MediaPlayer secondSound, secondFinalSound, periodChangeSound, startSound, emptySound;
    public MediaPlayer twoMinutesReady, twoMinutes, oneMinutesReady, oneMinute, fivety, fourty, thrity, twenty;
    public MediaPlayer pause, ten, eight, nine, seven, six, five, four, three, two, one;

    public Voiceover(Context context) {
        secondSound = MediaPlayer.create(context, R.raw.second_bell);
        secondFinalSound = MediaPlayer.create(context, R.raw.second_final_bell);
        periodChangeSound = MediaPlayer.create(context, R.raw.period_change_bell);
        startSound = MediaPlayer.create(context, R.raw.start_cow_bell);
        emptySound = MediaPlayer.create(context, R.raw.bicykle_bell);
        twoMinutesReady = MediaPlayer.create(context, R.raw.eng_two_minutes_ready);
        twoMinutes = MediaPlayer.create(context, R.raw.eng_two_minutes);
        oneMinutesReady = MediaPlayer.create(context, R.raw.eng_one_minute_ready);
        oneMinute = MediaPlayer.create(context, R.raw.eng_one_minute);
        fivety = MediaPlayer.create(context, R.raw.eng_fivety);
        fourty = MediaPlayer.create(context, R.raw.eng_fourty);
        thrity = MediaPlayer.create(context, R.raw.eng_threety);
        twenty = MediaPlayer.create(context, R.raw.eng_twenty);
        pause = MediaPlayer.create(context, R.raw.eng_pause);
        ten = MediaPlayer.create(context, R.raw.eng_ten);
        nine = MediaPlayer.create(context, R.raw.eng_nine);
        eight = MediaPlayer.create(context, R.raw.end_eight);
        seven = MediaPlayer.create(context, R.raw.eng_seven);
        six = MediaPlayer.create(context, R.raw.eng_six);
        five = MediaPlayer.create(context, R.raw.eng_five);
        four = MediaPlayer.create(context, R.raw.eng_four);
        three = MediaPlayer.create(context, R.raw.eng_three);
        two = MediaPlayer.create(context, R.raw.eng_two);
        one= MediaPlayer.create(context, R.raw.eng_one);
    }

    public void makeSound (MediaPlayer sound) {
        sound.start();
    }

}
