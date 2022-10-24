package com.example.racertimer.forecast.data.urlequest.JavaRequest;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MakeRequest extends AsyncTask<String, String, String> {
    private final static String PROJECT_LOG_TAG = "racer_timer, requestAsyncTask";

    private String line = "";
    private String connectionError = "connection error";
    private String errorDescription = "no error";

    private Handler handler; // handler для отправки результата заказчику

    private final String KEY = "fc35b8ee90f4ee45109149cc13ee7a4f"; // ключ аккаунта для сайта Openweathermap

    public MakeRequest(Handler handler) {
        this.handler = handler;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        // в Strings[0] приходит наш запрос, отправленный из вызвавшего метода
        HttpURLConnection httpURLConnection = null; // соединение
        BufferedReader bufferedReader = null; // читатель буфера
        Log.i(PROJECT_LOG_TAG, " Thread: " + Thread.currentThread().getName() + " preparing new URL reqest as: " + strings[0]);
        //Log.i("bugfix", "MakeRequest: making request: "+strings[0]);
        try {
            Log.i("bugfix", "MakeRequest: making request: rep1");

            URL url = new URL(strings[0]); // открываем ЮРЛ соединение
            Log.i("bugfix", "MakeRequest: making request: rep2");

            httpURLConnection = (HttpURLConnection) url.openConnection(); // открываем HTTP соединение
            Log.i("bugfix", "MakeRequest: making request: rep3");

            httpURLConnection.connect(); // соединяемся
            Log.i("bugfix", "MakeRequest: connected to URL... ");

            InputStream inputStream = httpURLConnection.getInputStream(); // считываем входящий поток
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream)); // запускаем
            // читатель буфера

            StringBuffer stringBuffer = new StringBuffer(); // буфер для записиданых из потока

            while ((line = bufferedReader.readLine()) != null) { // считываем входящий поток
                Log.i(PROJECT_LOG_TAG, " Thread: " + Thread.currentThread().getName() + " got new response line ");
                stringBuffer.append(line).append("\n"); // добавляем новую строку из потка
            }
            return stringBuffer.toString();
        } catch (MalformedURLException e) {
            errorDescription = e.toString();
            Log.i("bugfix", "MakeRequest: Error: " +errorDescription);
            e.printStackTrace();
        } catch (IOException e) {
            errorDescription = e.toString();
            Log.i("bugfix", "MakeRequest: Error: " +errorDescription);
            e.printStackTrace();
        } finally { // в конце работы
            if (httpURLConnection != null) httpURLConnection.disconnect(); // закрываем соединение
            if (bufferedReader != null) { // закрываем буфер
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    errorDescription = e.toString();
                    Log.i("bugfix", "MakeRequest: Error: " +errorDescription);
                    e.printStackTrace();
                }
            }
        }
        return connectionError; // если так и не поучили информацию, string получает ошибку
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (s != connectionError) {
            Log.i(PROJECT_LOG_TAG, " Thread: " + Thread.currentThread().getName() + " onPostExecute S = " + s);
            Message msg = handler.obtainMessage(1, s); // обертываем S в сообщение
            Log.i("bugfix", "MakeRequest: sending handler message with no error ");
            handler.sendMessage(msg); //отправляем сообщение
        } else {
            Message msg = handler.obtainMessage(1, errorDescription);
            Log.i("bugfix", "MakeRequest: sending handler message with ERROR ");
            handler.sendMessage(msg);
        }
    }
}
