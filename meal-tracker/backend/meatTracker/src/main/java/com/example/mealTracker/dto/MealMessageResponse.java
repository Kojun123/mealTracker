package com.example.mealTracker.dto;

import com.example.mealTracker.domain.MealItem;
import com.example.mealTracker.domain.TodaySummary;

import java.util.List;

public record MealMessageResponse(
        String assistantText,
        TodaySummary todaySummary,
        List<MealItem> items
) {
    public static MealMessageResponse normal(
            String assistantText,
            TodaySummary summary,
            List<MealItem> items
    ) {
        return new MealMessageResponse(assistantText, summary, items);
    }

    public static MealMessageResponse needConfirm(
            String message,
            TodaySummary summary,
            List<MealItem> items
    ) {
        return new MealMessageResponse(
                message,
                summary,
                items
        );
    }
}