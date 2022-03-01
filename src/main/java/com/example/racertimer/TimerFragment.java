package com.example.racertimer;

import static com.example.racertimer.multimedia.Voiceover1.SOUND_ASSET_EIGHT;
import static com.example.racertimer.multimedia.Voiceover1.SOUND_ASSET_FIFTY;
import static com.example.racertimer.multimedia.Voiceover1.SOUND_ASSET_FIVE;
import static com.example.racertimer.multimedia.Voiceover1.SOUND_ASSET_FORTY;
import static com.example.racertimer.multimedia.Voiceover1.SOUND_ASSET_FOUR;
import static com.example.racertimer.multimedia.Voiceover1.SOUND_ASSET_NINE;
import static com.example.racertimer.multimedia.Voiceover1.SOUND_ASSET_ONE;
import static com.example.racertimer.multimedia.Voiceover1.SOUND_ASSET_ONE_MINUTE;
import static com.example.racertimer.multimedia.Voiceover1.SOUND_ASSET_ONE_MINUTE_READY;
import static com.example.racertimer.multimedia.Voiceover1.SOUND_ASSET_PAUSE;
import static com.example.racertimer.multimedia.Voiceover1.SOUND_ASSET_SEVEN;
import static com.example.racertimer.multimedia.Voiceover1.SOUND_ASSET_SIX;
import static com.example.racertimer.multimedia.Voiceover1.SOUND_ASSET_START;
import static com.example.racertimer.multimedia.Voiceover1.SOUND_ASSET_TEN;
import static com.example.racertimer.multimedia.Voiceover1.SOUND_ASSET_THIRTY;
import static com.example.racertimer.multimedia.Voiceover1.SOUND_ASSET_THREE;
import static com.example.racertimer.multimedia.Voiceover1.SOUND_ASSET_TWENTY;
import static com.example.racertimer.multimedia.Voiceover1.SOUND_ASSET_TWO;
import static com.example.racertimer.multimedia.Voiceover1.SOUND_ASSET_TWO_MINUTE;
import static com.example.racertimer.multimedia.Voiceover1.SOUND_ASSET_TWO_MINUTE_READY;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.racertimer.multimedia.Voiceover1;

public class TimerFragment extends Fragment {
    private final static String PROJECT_LOG_TAG = "racer_timer";

    private CountDownTimer countDownTimer;
    private Voiceover1 voiceover1;
    private Button btnCancelTimer;
    private TextView timerResult;

    private String timerString = "00:00.00"; // переменная для вывода текущего секундомера чч:мм:сс.сот
    private int timerHour = 0; // переменная в часах
    private int timerMin = 0; // переменная счетчика в минутах
    private int timerSec = 0; // текущее значение таймера в сотых долей секунды

    private boolean timerPaused = true; // флаг: поставлен ли таймер на паузу

    public interface CloserTimerInterface {
        public void finishTheTimer();
    }

    CloserTimerInterface closerTimer;

    public TimerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            closerTimer = (CloserTimerInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " cast activity to interface is failed in TimerFragment");
        }
    }

    // метод аналог onCreate в активити, но без доступа к UI
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    // создание вью
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, // что передаем для отображения
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer, null);

//        runTimerCounter(60); // создаем счетчик для возможности выхода
//        countDownTimer.cancel();

        voiceover1 = new Voiceover1(getActivity());

        /** обработка нажатий и отображения циферблата */
        timerResult = view.findViewById(R.id.timer_fragment_tv);
        initTimer();
        timerResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timerPaused) { // если счетчик остановлен,
                    timerPaused = false; // снимамем счетчик с паузы
                    runTimerCounter(60); // запускаем счетчик на 1 минуту
                    //countDownTimer.start();
                    timerResult.setTextColor(Color.WHITE);
                } else { // если таймер идет, ставим на паузу
                    timerPaused = true;
                    if (countDownTimer != null ) countDownTimer.cancel();
                    timerResult.setTextColor(Color.RED);
                    voiceover1.playSingleTimerSound(SOUND_ASSET_PAUSE);
                }
            }
        });

        /** кнопка прекращение и закрытие таймера */
        Button butCancelRace = (Button) view.findViewById(R.id.cancel_race);
        butCancelRace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countDownTimer.cancel();
                closerTimer.finishTheTimer();
                // TODO: иногда появляется баг: вылет приложения при работе с таймером.
            }
        });

        /** кнопки корректировки таймера */
        Button but5Minutes = view.findViewById(R.id.set_5min);
        but5Minutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timerSec = 60 * 5;
                String timerString2Print = calcTimeMinSec(timerSec); // получаем стринговое отобржение таймера
                timerResult.setText(timerString2Print.toString()); // выводим значение на экран
            }
        });

        Button but3Minutes = view.findViewById(R.id.set_3min);
        but3Minutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timerSec = 60 * 3;
                String timerString2Print = calcTimeMinSec(timerSec); // получаем стринговое отобржение таймера
                timerResult.setText(timerString2Print.toString()); // выводим значение на экран
            }
        });

        Button but2Minutes = view.findViewById(R.id.set_2min);
        but2Minutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(PROJECT_LOG_TAG, " button 2 min pressed " );
                timerSec = 60 * 2;
                String timerString2Print = calcTimeMinSec(timerSec); // получаем стринговое отобржение таймера
                timerResult.setText(timerString2Print.toString()); // выводим значение на экран
            }
        });

        Button but1Minutes = view.findViewById(R.id.set_1min);
        but1Minutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(PROJECT_LOG_TAG, " button 1 min pressed " );
                timerSec = 60 * 1;
                String timerString2Print = calcTimeMinSec(timerSec); // получаем стринговое отобржение таймера
                timerResult.setText(timerString2Print.toString()); // выводим значение на экран
            }
        });
        return view;
    }

    private void initTimer() {
        //runTimerCounter(60); // запускаем счетчик на 1 минуту
        timerSec = 60 * 5; // устанавливаем таймер на 5 минут
        String timerString2Print = calcTimeMinSec(timerSec); // получаем стринговое отобржение таймера
        timerResult.setText(timerString2Print.toString()); // выводим значение на экран
        Toast.makeText(getActivity(), "tap the timer to START", Toast.LENGTH_LONG).show();
    }

    public void runTimerCounter(long timerSeconds) {
        countDownTimer = new CountDownTimer(timerSeconds * 1000, 1000) {
            @Override
            public void onTick(long l) { // действия во время отсчета
                if (timerSec > 0) onTimerTicked();
//                Log.i("Race", " Thread: "+Thread.currentThread().getName() + " timerSec = " +timerSec);

            }
            @Override
            protected void finalize() throws Throwable {
                super.finalize();
            }
            @Override
            public void onFinish() { // действия по окончании отсчета
                if (timerSec > 0) runTimerCounter(timerSec * 1000);
            }
        }.start();
    }

    /** обновление и обработка данных таймера*/
    private void onTimerTicked () { // таймер обновился
        String timerString2Print = "00:00";
//        if (! timerPaused){
            timerSec -- ;
            switch (timerSec) {
                case 130: voiceover1.playSingleTimerSound(SOUND_ASSET_TWO_MINUTE_READY); break;
                case 120: voiceover1.playSingleTimerSound(SOUND_ASSET_TWO_MINUTE); break;
                case 70: voiceover1.playSingleTimerSound(SOUND_ASSET_ONE_MINUTE_READY); break;
                case 60: voiceover1.playSingleTimerSound(SOUND_ASSET_ONE_MINUTE); break;
                case 50: voiceover1.playSingleTimerSound(SOUND_ASSET_FIFTY); break;
                case 40: voiceover1.playSingleTimerSound(SOUND_ASSET_FORTY); break;
                case 30: voiceover1.playSingleTimerSound(SOUND_ASSET_THIRTY); break;
                case 20: voiceover1.playSingleTimerSound(SOUND_ASSET_TWENTY); break;
                case 10: voiceover1.playSingleTimerSound(SOUND_ASSET_TEN); break;
                case 9: voiceover1.playSingleTimerSound(SOUND_ASSET_NINE); break;
                case 8: voiceover1.playSingleTimerSound(SOUND_ASSET_EIGHT); break;
                case 7: voiceover1.playSingleTimerSound(SOUND_ASSET_SEVEN); break;
                case 6: voiceover1.playSingleTimerSound(SOUND_ASSET_SIX); break;
                case 5: voiceover1.playSingleTimerSound(SOUND_ASSET_FIVE); break;
                case 4: voiceover1.playSingleTimerSound(SOUND_ASSET_FOUR); break;
                case 3: voiceover1.playSingleTimerSound(SOUND_ASSET_THREE); break;
                case 2: voiceover1.playSingleTimerSound(SOUND_ASSET_TWO); break;
                case 1: voiceover1.playSingleTimerSound(SOUND_ASSET_ONE); break;
                case 0: voiceover1.playSingleTimerSound(SOUND_ASSET_START);
                    closerTimer.finishTheTimer();
                    countDownTimer.cancel();
                    timerPaused = true;
                default: break;
           // }
        }

        timerString2Print = calcTimeMinSec(timerSec); // получаем стринговое отобржение таймера
        if (timerSec <= 0) timerString2Print = "GO!!!";
        timerResult.setText(timerString2Print.toString()); // выводим значение на экран
    }
    /** получение стринговых показаний для TextView */
    public String calcTimeMinSec (int timerSec) {
        String result=null;
        int sec;
        int min;
        min = timerSec / 60;
        result = min + ":00";
        sec = timerSec % 60;
        if (sec !=0) result = min + ":" + sec;
        if (sec < 10) result = min + ":0" + sec;
        return result;
    }
}