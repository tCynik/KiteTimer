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

    public MakeRequest(Handler handler) {
        this.handler = handler;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        HttpURLConnection httpURLConnection = null; // соединение
        BufferedReader bufferedReader = null; // читатель буфера
        Log.i(PROJECT_LOG_TAG, " Thread: " + Thread.currentThread().getName() + " preparing new URL reqest as: " + strings[0]);
        try {
            URL url = new URL(strings[0]); // открываем ЮРЛ соединение
            httpURLConnection = (HttpURLConnection) url.openConnection(); // открываем HTTP соединение
            httpURLConnection.connect(); // соединяемся
            InputStream inputStream = httpURLConnection.getInputStream(); // считываем входящий поток
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuffer stringBuffer = new StringBuffer(); // буфер для записи даных из потока

            while ((line = bufferedReader.readLine()) != null) { // считываем входящий поток
                stringBuffer.append(line).append("\n"); // добавляем новую строку из потка
            }
            return stringBuffer.toString();
        } catch (MalformedURLException e) {
            Log.i(PROJECT_LOG_TAG, "ERROR: MalformedURLException = " +e);
            e.printStackTrace();
        } catch (IOException e) {
            Log.i("bugfix", "ERROR: IOException = " +e);
            e.printStackTrace();
        } finally { // в конце работы
            if (httpURLConnection != null) httpURLConnection.disconnect(); // закрываем соединение
            if (bufferedReader != null) { // закрываем буфер
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    Log.i("bugfix", "ERROR: IOException = " +e);
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
            handler.sendMessage(msg); //отправляем сообщение
        } else {
            Message msg = handler.obtainMessage(1, errorDescription);
            handler.sendMessage(msg);
        }
    }
}
