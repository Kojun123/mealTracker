package com.example.mealTracker.dto;

import com.example.mealTracker.domain.MealItem;
import com.example.mealTracker.domain.TodaySummary;

import java.util.List;

public record MealMessageResponse(
        String assistantText,
        TodaySummary todaySummary,
        List<MealItem> items
) {}