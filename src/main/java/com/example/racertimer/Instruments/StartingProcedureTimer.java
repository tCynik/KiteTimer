package com.example.racertimer.Instruments;

import static com.example.racertimer.multimedia.TimerVoiceover.SOUND_ASSET_EIGHT;
import static com.example.racertimer.multimedia.TimerVoiceover.SOUND_ASSET_FIFTY;
import static com.example.racertimer.multimedia.TimerVoiceover.SOUND_ASSET_FIVE;
import static com.example.racertimer.multimedia.TimerVoiceover.SOUND_ASSET_FORTY;
import static com.example.racertimer.multimedia.TimerVoiceover.SOUND_ASSET_FOUR;
import static com.example.racertimer.multimedia.TimerVoiceover.SOUND_ASSET_NINE;
import static com.example.racertimer.multimedia.TimerVoiceover.SOUND_ASSET_ONE;
import static com.example.racertimer.multimedia.TimerVoiceover.SOUND_ASSET_ONE_MINUTE;
import static com.example.racertimer.multimedia.TimerVoiceover.SOUND_ASSET_ONE_MINUTE_READY;
import static com.example.racertimer.multimedia.TimerVoiceover.SOUND_ASSET_PAUSE;
import static com.example.racertimer.multimedia.TimerVoiceover.SOUND_ASSET_SEVEN;
import static com.example.racertimer.multimedia.TimerVoiceover.SOUND_ASSET_SIX;
import static com.example.racertimer.multimedia.TimerVoiceover.SOUND_ASSET_START;
import static com.example.racertimer.multimedia.TimerVoiceover.SOUND_ASSET_TEN;
import static com.example.racertimer.multimedia.TimerVoiceover.SOUND_ASSET_THIRTY;
import static com.example.racertimer.multimedia.TimerVoiceover.SOUND_ASSET_THREE;
import static com.example.racertimer.multimedia.TimerVoiceover.SOUND_ASSET_TWENTY;
import static com.example.racertimer.multimedia.TimerVoiceover.SOUND_ASSET_TWO;
import static com.example.racertimer.multimedia.TimerVoiceover.SOUND_ASSET_TWO_MINUTE;
import static com.example.racertimer.multimedia.TimerVoiceover.SOUND_ASSET_TWO_MINUTE_READY;

import com.example.racertimer.mainActivity.MainActivity;
import com.example.racertimer.multimedia.TimerVoiceover;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class StartingProcedureTimer extends MyTimer {
    long currentTimePeriod;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
    TimerVoiceover voiceover;

    public StartingProcedureTimer(MainActivity mainActivity, InfoBarStatusUpdater infoBarStatusUpdater) {
        super(mainActivity, infoBarStatusUpdater, 1000, 60, "mm:ss"); // период обновления счетчика 1 секунда
        voiceover = new TimerVoiceover(mainActivity);
    }

    @Override
    long calculateTimerLeft(Date currentTime) {
        return startedTime.getTime() + currentTimePeriod - currentTime.getTime();
    }

    public void setTimerPeriod(long currentTimerSize) {
        startedTime = Calendar.getInstance().getTime();
        this.currentTimePeriod = currentTimerSize;
        String timerStatusString = simpleDateFormat.format(currentTimerSize);
        infoBarStatusUpdater.onTimerStatusUpdated(timerStatusString);
        voiceoverTimerStatus(currentTimerSize);
    }

    @Override
    public void stop() {
        super.stop();
        currentTimePeriod = timerLeft;
    }

    public void pause() {
        voiceover.playSound(SOUND_ASSET_PAUSE);
        stop();
    }

    @Override
    void onTimerTicked(long timerLeft) {
        super.onTimerTicked(timerLeft);
        voiceoverTimerStatus(timerLeft);
    }

    private void voiceoverTimerStatus(long timerLeft) {
        int timerLeftSeconds = (int) timerLeft / 1000;
        switch (timerLeftSeconds) {
            case 130: voiceover.playSound(SOUND_ASSET_TWO_MINUTE_READY);
                break;
            case 120: voiceover.playSound(SOUND_ASSET_TWO_MINUTE);
                break;
            case 70: voiceover.playSound(SOUND_ASSET_ONE_MINUTE_READY);
                break;
            case 60: voiceover.playSound(SOUND_ASSET_ONE_MINUTE);
                break;
            case 50: voiceover.playSound(SOUND_ASSET_FIFTY);
                break;
            case 40: voiceover.playSound(SOUND_ASSET_FORTY);
                break;
            case 30: voiceover.playSound(SOUND_ASSET_THIRTY);
                break;
            case 20: voiceover.playSound(SOUND_ASSET_TWENTY);
                break;
            case 10: voiceover.playSound(SOUND_ASSET_TEN);
                break;
            case 9: voiceover.playSound(SOUND_ASSET_NINE);
                break;
            case 8: voiceover.playSound(SOUND_ASSET_EIGHT);
                break;
            case 7: voiceover.playSound(SOUND_ASSET_SEVEN);
                break;
            case 6: voiceover.playSound(SOUND_ASSET_SIX);
                break;
            case 5: voiceover.playSound(SOUND_ASSET_FIVE);
                break;
            case 4: voiceover.playSound(SOUND_ASSET_FOUR);
                break;
            case 3: voiceover.playSound(SOUND_ASSET_THREE);
                mainActivity.onStartingTimerPeriodChanged("timer three");
                break;
            case 2: voiceover.playSound(SOUND_ASSET_TWO);
                mainActivity.onStartingTimerPeriodChanged("timer two");
                break;
            case 1: voiceover.playSound(SOUND_ASSET_ONE);
                mainActivity.onStartingTimerPeriodChanged("timer one");
                break;
            case 0:
                voiceover.playSound(SOUND_ASSET_START);
                break;
            default:
                break;
        }
    }
}
