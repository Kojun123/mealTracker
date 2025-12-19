package com.example.mealTracker.dto;

import java.util.List;

public record MealMessageResponse(
        String assistantText,
        TodaySummary todaySummary,
        List<MealItem> items
) {}