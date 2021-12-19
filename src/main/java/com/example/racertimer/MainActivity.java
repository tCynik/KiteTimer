package com.example.racertimer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.racertimer.GPSContent.MainLocal;

////////////// это меню главного экрана, в котором выбираем тип стартовой процедуры - 5 минут, 3 минуты, немедленный старт.
////////////// после выбора типа процедуры открывается окно стартового таймера:
////////////// после нажатия Instant start открывается экран гонки
////////////// при запуске главного экрана происходит запуск работы GPS модуля

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button butTimer5Min, butTimer3Min; // кнопки выбора стартовой процедуры
    private Button butTimerInstant; // кнопка немедленного начала гонки

    private TextView textTime; // переменная времени в левом вехнем углу (дата и время)

    private MainLocal mainLocal; // поле класса Mainlocal для вызова функции запуска LocationManager

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        butTimer5Min = findViewById(R.id.but_5mins); // Заводим кнопки таймеров
        butTimer5Min.setOnClickListener(this);
        butTimer3Min = findViewById(R.id.but_3mins);
        butTimer3Min.setOnClickListener(this);
        butTimerInstant = findViewById(R.id.but_instant);
        butTimerInstant.setOnClickListener(this);

        textTime = findViewById(R.id.currentTime);

        ///// карочи, долбаная срань с этим GPSом. Все в кучу.
        ///// проблемы: 1. запуск приемника - из мэйна, получение данных - экранами timer и Race
        ///// нужно переносить все в отдельные классы, и туда обращаться


//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Test1");
//        builder.setCancelable(true);
//        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // Кнопка ОК
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss(); // Отпускает диалоговое окно
//            }
//        });
//        AlertDialog dialog = builder.create();

        MainLocal mainlocal = new MainLocal();
        mainlocal.initLocationManager(); // запускаем LocationManager

    }

    @Override
    public void onClick(View view) { // view - элемент, на который произошло нажатие (его id)
        Context context = this; // создаем контекст относительно текущего активити
        Class nextActivity = ActivityTimer.class; // активити, в которое будем переходить чаще всего
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.but_5mins:
                intent = new Intent(context, nextActivity); // по умолчанию 5 минут (ничего не передаем)
                break;
            case R.id.but_3mins:
                intent = new Intent(context, nextActivity); // при трехминутке передаем тайминг 3 мин
                intent.putExtra("procedureTiming", 3);
                break;
            case R.id.but_instant:
                intent = new Intent(context, ActivityRace.class);
            default:
                break;
        }
        startActivity(intent);
    }
}