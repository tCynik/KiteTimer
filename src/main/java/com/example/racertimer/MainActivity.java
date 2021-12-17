package com.example.racertimer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

////////////// это меню главного экрана, в котором выбираем ти стартовой процедуры - 5 минут, 3 минуты, немедленный старт.
////////////// в 5 минутном открывается окно таймера со следующими режимами:
////////////// 5 минут, кнопки "главное меню", "5 минут" (таймер на 5:00), "2 минуты" (таймер на 2:00)
////////////// по достижении 2 минут переключение на экран с кнопками: "5 минут", "2 минуты", "1 минута"
////////////// по достижении 1 минуты экран с кнопками "2 минуты", "1 минута", "старт"

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button butTimer5Min; // кнопка выбора 5 минутной процедуры
    private Button butTimer3Min; // кнопка выбора 3 минутной процедуры
    private Button butTimerInstant; // кнопка немедленного начала гонки

    private TextView textTime; // переменная времени в левом вехнем углу

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

//        View.OnClickListener timer5Min = new View.OnClickListener() { // описываем нажатие первой кнопки
//            @Override
//            public void onClick(View view) {
//                // переход на экран 5 минутной процедуры
//            }
//        };
//        butTimer5Min.setOnClickListener(timer5Min);
//
//        View.OnClickListener timer3Min = new View.OnClickListener() { // нажатие второй кнопки
//            @Override
//            public void onClick(View view) {
//                ////////// вот тут по идее должен быть переход на экран 3 минутной процедуры Screen3Min
//            }
//        };
//        butTimer3Min.setOnClickListener(timer3Min);

    }

    @Override
    public void onClick(View view) { // view - элемент, на который произошло нажатие (его id)
        Intent intent = new Intent();
        switch (view.getId()){
            case R.id.but_5mins:
                intent = new Intent(this, ActivityTimer.class); // this - элемент view???
                break;
            case R.id.but_3mins:
                intent = new Intent(this, ActivityTimer.class);
                break;
            case R.id.but_instant:
                intent = new Intent (this, ActivityRace.class);
            default: break;
        }

        startActivity(intent);
    }
}