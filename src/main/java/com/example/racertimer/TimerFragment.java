package com.example.racertimer;

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

import com.example.racertimer.multimedia.Voiceover;

public class TimerFragment extends Fragment {
    private final static String PROJECT_LOG_TAG = "racer_timer";

    private CountDownTimer countDownTimer;
    private Voiceover voiceover;
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

        voiceover = new Voiceover(getActivity()); // создаем озвучку

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
                    countDownTimer.cancel();
                    timerResult.setTextColor(Color.RED);
//                    voiceover.playSoundOnce(voiceover.pauseSID);
                    voiceover.makeSound(voiceover.pause);
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
                // TODO: надо протестить остановку таймера и минутные кнопки: не всегда останавливается отсчет.
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
                case 130: voiceover.makeSound(voiceover.twoMinutesReady); break;
                case 120: voiceover.makeSound(voiceover.twoMinutes); break;
                case 70: voiceover.makeSound(voiceover.oneMinutesReady); break;
                case 60: voiceover.makeSound(voiceover.oneMinute); break;
                case 50: voiceover.makeSound(voiceover.fivety); break;
                case 40: voiceover.makeSound(voiceover.fourty); break;
                case 30: voiceover.makeSound(voiceover.thrity); break;
                case 20: voiceover.makeSound(voiceover.twenty); break;
                case 10: voiceover.makeSound(voiceover.ten); break;
                case 9: voiceover.makeSound(voiceover.nine); break;
                case 8: voiceover.makeSound(voiceover.eight); break;
                case 7: voiceover.makeSound(voiceover.seven); break;
                case 6: voiceover.makeSound(voiceover.six); break;
                case 5: voiceover.makeSound(voiceover.five); break;
                case 4: voiceover.makeSound(voiceover.four); break;
                case 3: voiceover.makeSound(voiceover.three); break;
                case 2: voiceover.makeSound(voiceover.two); break;
                case 1: voiceover.makeSound(voiceover.one); break;
                case 0: voiceover.makeSound(voiceover.startSound);
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