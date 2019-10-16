package com.geekbrains.city_weather;

import android.content.Context;

import java.util.Random;

public class WeatherBuilder {
    public String getWhether(Context context) {
        String result;
        Random random = new Random();
        int rnd = random.nextInt(5);
        if (rnd == 0) {
            result = context.getString(R.string.Sun);
        } else if (rnd == 1) {
            result = context.getString(R.string.var_clouds);
        } else if (rnd == 2) {
            result = context.getString(R.string.little_rain);
        } else if (rnd == 3) {
            result = context.getString(R.string.rain);
        } else {
            result = context.getString(R.string.boom);
        }
        return result;
    }
}