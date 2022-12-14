package com.tcynik.racertimer.forecast.data.network.urlequest;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

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
                try {
                    String message = String.valueOf(msg.obj);
                    try {
                        JSONObject jsonObject = new JSONObject(message);
                        resultJsonInterface.gotResult(jsonObject); //onJSONUpdated (jsonObject); // отправляем на обработку
                    } catch (JSONException e) {
                        resultJsonInterface.errorOccurs(e+": "+ message);
                    }
                } catch (Exception e) {
                    resultJsonInterface.errorOccurs(e.toString());
                }
            }
        };
    }
}

