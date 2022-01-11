package com.example.racertimer.multimedia;

import android.content.Context;
import android.media.MediaPlayer;

import com.example.racertimer.R;

public class Voiceover {
    private MediaPlayer secondSound, secondFinalSound, periodChangeSound, startSound, emptySound;

    public Voiceover(Context context) {
        secondSound = MediaPlayer.create(context, R.raw.second_bell);
        secondFinalSound = MediaPlayer.create(context, R.raw.second_final_bell);
        periodChangeSound = MediaPlayer.create(context, R.raw.period_change_bell);
        startSound = MediaPlayer.create(context, R.raw.start_cow_bell);
        emptySound = MediaPlayer.create(context, R.raw.bicykle_bell);
    }

    public void makeSound (String nameSound) {
        MediaPlayer sound;
        switch (nameSound) {
            case "secondSound":
                sound = secondSound;
                break;
            case "secondFinalSound":
                sound = secondFinalSound;
                break;
            case "periodChangeSound":
                sound = periodChangeSound;
                break;
            case "startSound":
                sound = startSound;
                break;
            default:
                sound = emptySound;
        }
        sound.start();
    }

}
