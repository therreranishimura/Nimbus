package com.example.nubes;

import java.util.List;

public class WeatherResponse {
    public Results results;

    public static class Results {
        public String city;
        public int temp;
        public String description;
        public List<Forecast> forecast;
    }

    public static class Forecast {
        public String date;
        public String weekday;
        public int max;
        public int min;
        public String description;
    }
}
