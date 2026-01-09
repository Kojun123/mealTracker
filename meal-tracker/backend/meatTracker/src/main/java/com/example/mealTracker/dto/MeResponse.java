package com.example.mealTracker.dto;

public record MeResponse(
        String email,
        int targetCalories,
        int targetProtein
) {
}
