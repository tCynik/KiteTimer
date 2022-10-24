package com.example.racertimer.Instruments;

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

public class ForecastManager {
    private final static String PROJECT_LOG_TAG = "racer_timer, forecastManager";

    private static final String WEBSITE_FORECAST = "https://api.openweathermap.org/data/2.5";
    private static final String FORECAST_ACTION = "/forecast?";
    private static final String WEBSITE_KEY = "fc35b8ee90f4ee45109149cc13ee7a4f";
    private Handler handler;
    double latitude ;
    double longitude ;


    public ForecastManager(Handler handler){ // в конструктор передается хендлер для обратной связи
        this.handler = handler;
    }

    public void updateForecast (double latitude, double longitude) {
        Log.i(PROJECT_LOG_TAG, " Thread: "+Thread.currentThread().getName() + ", updating forecast starting" );
        this.latitude = latitude;
        this.longitude = longitude;
        Log.i("bugfix", "ForecastManager1: updating forecast");
        String URLRequest = sintezateURL();
        MakeRequest makeRequest = new MakeRequest(handler);
        makeRequest.execute(URLRequest);
        Log.i("bugfix", "ForecastManager1: ending updating forecast");
    }

    private String sintezateURL () {
        String URLRequest = WEBSITE_FORECAST + FORECAST_ACTION+ "lat=" + latitude + "&lon=" + longitude +
                "&appid=" + WEBSITE_KEY + "&units=metric";
        Log.i(PROJECT_LOG_TAG, " Thread: "+Thread.currentThread().getName() + ", making URL request: "+ URLRequest );
        return URLRequest;
    }
}

class MakeRequest extends AsyncTask<String, String, String> {
    private final static String PROJECT_LOG_TAG = "racer_timer, requestAsyncTask";

    private String line = "";
    private String connectionError = "connection error";

    private Handler handler; // handler для отправки результата заказчику

    private final String KEY = "fc35b8ee90f4ee45109149cc13ee7a4f"; // ключ аккаунта для сайта Openweathermap

    public MakeRequest (Handler handler){
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
        Log.i(PROJECT_LOG_TAG, " Thread: "+Thread.currentThread().getName() + " preparing new URL reqest as: " + strings[0]);
        Log.i("bugfix", "making URL request: "+ strings[0] );
        try{
            Log.i("bugfix", "starting URL request: "+ strings[0] );

            URL url = new URL(strings[0]); // открываем ЮРЛ соединение

            httpURLConnection = (HttpURLConnection) url.openConnection(); // открываем HTTP соединение
            httpURLConnection.connect(); // соединяемся

            InputStream inputStream = httpURLConnection.getInputStream(); // считываем входящий поток
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream)); // запускаем
            // читатель буфера

            StringBuffer stringBuffer = new StringBuffer(); // буфер для записиданых из потока

            while ((line = bufferedReader.readLine()) != null ) { // считываем входящий поток
                Log.i(PROJECT_LOG_TAG, " Thread: " + Thread.currentThread().getName() + " got new response line ");
                Log.i("bugfix", " got new response line ");
                stringBuffer.append(line).append("\n"); // добавляем новую строку из потка
            }
            return stringBuffer.toString();
        } catch (MalformedURLException e) {
            Log.i("bugfix", "ForecastManager1: MalformedURLException: "+ e);
            e.printStackTrace();
        } catch (IOException e) {
            Log.i("bugfix", "ForecastManager1: IOException: "+ e);
            e.printStackTrace();
        } finally { // в конце работы
            if (httpURLConnection != null ) httpURLConnection.disconnect(); // закрываем соединение
            if (bufferedReader != null) { // закрываем буфер
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    Log.i("bugfix", "ForecastManager1: finally IOException: "+ e);
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
            Log.i(PROJECT_LOG_TAG, " Thread: "+Thread.currentThread().getName() +" onPostExecute S = "+ s);
            Log.i("bugfix", " onPostExecute message S = "+ s);
            Message msg = handler.obtainMessage(1, s); // обертываем S в сообщение
            handler.sendMessage(msg); //отправляем сообщение
        }
    }
}
