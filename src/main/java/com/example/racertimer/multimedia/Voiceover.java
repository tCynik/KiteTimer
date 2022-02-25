package com.example.racertimer.multimedia;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

import com.example.racertimer.R;

public class Voiceover {
    // звуки в формате SoundPool
    public SoundPool twoMinutesReadySP, twoMinutesSP, oneMinutesReadySP, oneMinuteSP, fivetySP, fourtySP, thritySP, twentySP;
    public SoundPool pauseSP, tenSP, eightSP, nineSP, sevenSP, sixSP, fiveSP, fourSP, threeSP, twoSP, oneSP, beepSP;
    public SoundPoolID pauseSID, tenSID, eightSID, nineSID, sevenSID, sixSID, fiveSID, fourSID, threeSID, twoSID, oneSID, beepSID;

    // звуки в формате MediaPlayer
    public MediaPlayer secondSound, secondFinalSound, periodChangeSound, startSound, emptySound;
    public MediaPlayer twoMinutesReady, twoMinutes, oneMinutesReady, oneMinute, fivety, fourty, thrity, twenty;
    public MediaPlayer pause, ten, eight, nine, seven, six, five, four, three, two, one;

    private AudioManager audioManager;

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
//    audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//
//    pauseSID = new SoundPoolID(4, AudioManager.STREAM_MUSIC, 0);
//    pauseSID.setSoundID(pauseSID.load(context, R.raw.eng_pause, 2));
//
//        pauseSP = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
//    pauseSP.load(context, R.raw.eng_pause, 2);
//
////    tenSP.load(context, R.raw.eng_ten, 2);
////    eightSP.load(context, R.raw.end_eight, 2);
////    nineSP.load(context, R.raw.eng_nine, 2);
////    sevenSP.load(context, R.raw.eng_seven, 2);
////    sixSP.load(context, R.raw.eng_six, 2);
////    fiveSP.load(context, R.raw.eng_five, 2);
////    fourSP.load(context, R.raw.eng_four, 2);
////    threeSP.load(context, R.raw.eng_three, 2);
////    twoSP.load(context, R.raw.eng_two, 2);
////    oneSP.load(context, R.raw.eng_one, 2);
//        beepSID = new SoundPoolID(4, AudioManager.STREAM_MUSIC, 0);
//        beepSID.load(context, R.raw.beep2long, 1);
////    beepSP.load(context, R.raw.beep2long, 1);
////
////    twoMinutesReadySP.load(context, R.raw.eng_two_minutes_ready, 2);
////    twoMinutesSP.load(context, R.raw.eng_two_minutes, 2);
////    oneMinutesReadySP.load(context, R.raw.eng_one_minute_ready, 2);
////    oneMinuteSP.load(context, R.raw.eng_one_minute, 2);
////    fivetySP.load(context, R.raw.eng_fivety, 2);
////    fourtySP.load(context, R.raw.eng_fourty, 2);
////    thritySP.load(context, R.raw.eng_threety, 2);
////    twentySP.load(context, R.raw.eng_twenty, 2);
    }

    public void playSoundOnce (SoundPoolID soundPoolName) {
        soundPoolName.play(soundPoolName.getSoundID(), 1, 1, 2, 0, 1);
    }

    public int playSoundLoop (SoundPoolID soundPoolName, float playbackspeed) {
        int streamId; // номер проигрываемого потока для управления проигрыванием
        streamId = soundPoolName.play(soundPoolName.getSoundID(), 1, 1, 1, 1, playbackspeed);
        return streamId;
    }

    public void stopPlaying (SoundPoolID soundPoolID, int soundId) {
        soundPoolID.stop(soundId);
    }

    public void makeSound (MediaPlayer sound) {
        sound.start();
    }


}
