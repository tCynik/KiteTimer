package com.example.racertimer.Instruments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.racertimer.R;
import com.example.racertimer.windDirection.WindChangedHerald;

/** класс ручного ввода направления ветра через диалоговое окно */
public class ManuallyWind implements SeekBar.OnSeekBarChangeListener {
    Context context;
    private int windDirection;
    private LayoutInflater windDialogLayoutInflater;
    private View windDialogView; // наш лайаут

    private EditText inputText; // поле ввода
    private SeekBar seekbarWind; // бар настройки ветра
    private Button buttonIncrease, buttonDecrease;

    private WindChangedHerald windChangedHerald;

    public ManuallyWind (Context context, int windDirection, WindChangedHerald windChangedHerald) {
        this.context = context;
        this.windDirection = windDirection;
        this.windChangedHerald = windChangedHerald;
        windDialogLayoutInflater = LayoutInflater.from(context);
        windDialogView = windDialogLayoutInflater.inflate(R.layout.manually_input_wind, null);
        inputText = windDialogView.findViewById(R.id.edit_wind);
        seekbarWind = windDialogView.findViewById(R.id.sb_direction);
        buttonIncrease = windDialogView.findViewById(R.id.increase_btn);
        buttonDecrease = windDialogView.findViewById(R.id.decrease_btn);

        seekbarWind.setProgress(windDirection); // устанавливаем бегунок на текущую позицию
    }

    public void showView () { // метод вывода вью для настройки направления ветра
        AlertDialog.Builder windDialogBuilder = new AlertDialog.Builder(context); // строитель диалога
        windDialogBuilder.setView(windDialogView);
        inputText.setHint(String.valueOf(windDirection));
        seekbarWind.setOnSeekBarChangeListener((SeekBar.OnSeekBarChangeListener) this);

        View.OnClickListener increaseButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int updatedWindDirection = CoursesCalculator.convertAngleFrom0To360(windDirection + 1);
                onWindChanged(updatedWindDirection);
                seekbarWind.setProgress(updatedWindDirection);
            }
        };

        View.OnClickListener decreaseButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int updatedWindDirection = CoursesCalculator.convertAngleFrom0To360(windDirection - 1);
                onWindChanged(updatedWindDirection);
                seekbarWind.setProgress(updatedWindDirection);
            }
        };

        buttonIncrease.setOnClickListener(increaseButtonListener);
        buttonDecrease.setOnClickListener(decreaseButtonListener);

        windDialogBuilder
                .setCancelable(true) // отменяемый (при нажатии вне поля диалога закрывается)
                // назначаем кнопки взаимодействия
                .setPositiveButton("set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i("racer_timer_tools_fragment", " toasting = " + inputText.getText() );
                        Toast.makeText(context, "new wind dir = " + inputText.getText(), Toast.LENGTH_LONG);
                        int windDirection = 10000; // первоначальное значение 10000 = данных нет
                        try { // преобразуем полученный стринг в int
                            windDirection = Integer.parseInt(String.valueOf(inputText.getText()));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }

                        if (windDirection != 10000) { // если есть новые данные, передаем в активити
                            windChangedHerald.onWindDirectionChanged(windDirection, WindProvider.MANUAL);
//                            MainActivity mainActivity = (MainActivity) context;
//                            mainActivity.onWindDirectionChanged(windDirection);
                        }
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alertDialog = windDialogBuilder.create(); // создание диалога
        alertDialog.setTitle("Set wind direction (title)"); // заголовок
        alertDialog.show(); // отображение диалога
    }

    private void onWindChanged (int updatedWindDir) {
        windDirection = updatedWindDir;
        inputText.setText(String.valueOf(windDirection));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (seekBar == seekbarWind) {
            Log.i("racer_timer_tools_fragment", " seekbar wind = " + i );
            onWindChanged(i);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}

// TODO: для вылизывания: сделать синхронизацию прогрессбара при изменении значения в inputText вводом