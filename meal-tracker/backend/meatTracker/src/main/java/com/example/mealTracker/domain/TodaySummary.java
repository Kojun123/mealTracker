package com.example.mealTracker.domain;

import lombok.Data;

@Data
public class TodaySummary {
    private double totalCalories;
    private double totalProtein;
    private double goalCalories;
    private double goalProtein;


    public TodaySummary(double totalCalories, double totalProtein, double goalCalories, double goalProtein) {
        this.totalCalories = totalCalories;
        this.totalProtein = totalProtein;
        this.goalCalories = goalCalories;
        this.goalProtein = goalProtein;
    }
}