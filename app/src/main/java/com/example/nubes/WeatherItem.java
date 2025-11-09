package com.example.nubes;

public class WeatherItem {
    private String city;
    private String temperature;
    private String condition;
    private int iconResId;

    public WeatherItem(String city, String temperature, String condition, int iconResId) {
        this.city = city;
        this.temperature = temperature;
        this.condition = condition;
        this.iconResId = iconResId;

    }

    public String getCity() {
        return city;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getCondition() {
        return condition;
    }

    public int getIconResId() { return iconResId; }
}
