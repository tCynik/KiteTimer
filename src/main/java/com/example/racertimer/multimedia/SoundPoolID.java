package com.example.racertimer.multimedia;

import android.media.SoundPool;

public class SoundPoolID extends SoundPool {
    private int soundID;
    /**
     * @param maxStreams
     * @param streamType
     * @param srcQuality
     * @deprecated
     */
    public SoundPoolID(int maxStreams, int streamType, int srcQuality) {
        super(maxStreams, streamType, srcQuality);
    }

    public void setSoundID (int soundID) {
        this.soundID = soundID;
    }

    public int getSoundID () {
        return this.soundID;
    }

}
