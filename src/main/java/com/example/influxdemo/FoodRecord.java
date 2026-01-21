package com.example.influxdemo;


public class FoodRecord {
    private String time;
    private String name;
    private double calories;

    public FoodRecord() {}

    public FoodRecord(String time, String name, double calories) {
        this.time = time;
        this.name = name;
        this.calories = calories;
    }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getCalories() { return calories; }
    public void setCalories(double calories) { this.calories = calories; }
}
