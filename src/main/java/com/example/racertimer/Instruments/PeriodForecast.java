package com.example.racertimer.Instruments;

public class PeriodForecast {
    private long time;
    private double temp;
    private int windDirection;
    private double windSpeed, windGust;

    public PeriodForecast (long time, double temp, int windDirection, double windSpeed, double windGust) {
        this.time = time;
        this.temp = temp;
        this.windDirection = windDirection;
        this.windSpeed = windSpeed;
        this.windGust = windGust;
    }

    public long getTime() {
        return time;
    }

    public double getTemp() {
        return temp;
    }

    public int getWindDirection() {
        return windDirection;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public double getWindGust() {
        return windGust;
    }
}
