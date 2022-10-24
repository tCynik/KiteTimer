package com.example.racertimer.forecast.data.urlequest.JavaRequest;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.racertimer.forecast.data.urlequest.ResultJsonInterface;

import org.json.JSONException;
import org.json.JSONObject;

public class JavaForecastManager {
    private Handler handler;
    private ResultJsonInterface resultJsonInterface;

    public JavaForecastManager(ResultJsonInterface resultJsonInterface){ // в конструктор передается хендлер для обратной связи
        handler = createHandler();
        this.resultJsonInterface = resultJsonInterface;
    }

    public void updateForecast (String requestString) {
        MakeRequest makeRequest = new MakeRequest(handler);
        makeRequest.execute(requestString);
    }

    private Handler createHandler() {
        return new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) { // при получении сообщения handler (в виде String)
                super.handleMessage(msg);
//                try {
//                    JSONObject jsonObject = new JSONObject(String.valueOf(msg.obj)); // превращаем его в Json
//                    resultJsonInterface.gotResult(jsonObject); //onJSONUpdated (jsonObject); // отправляем на обработку
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    resultJsonInterface.errorOccurs(e.toString());
//                }
                try {
                    String message = String.valueOf(msg.obj);
                    try {
                        JSONObject jsonObject = new JSONObject(message);
                        Log.i("bugfix", "JavaForecastManager: has JSON, making message ");
                        resultJsonInterface.gotResult(jsonObject); //onJSONUpdated (jsonObject); // отправляем на обработку
                    } catch (JSONException e) {
                        Log.i("bugfix", "JavaForecastManager: has JSON error, making error message ");
                        resultJsonInterface.errorOccurs(e+": "+ message);
                    }
                } catch (Exception e) {
                    Log.i("bugfix", "JavaForecastManager: no JSON, just error - sending message");
                    resultJsonInterface.errorOccurs(e.toString());
                }
            }
        };
    }
}

