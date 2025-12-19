package com.example.mealTracker.service.model;

public record Nutrition(double caloriesPer, double proteinPer) {
    public static Nutrition unknown() {
        return new Nutrition(0, 0);
    }
}