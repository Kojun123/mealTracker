package com.example.mealTracker.dto;

import com.example.mealTracker.domain.FoodMaster;
import com.example.mealTracker.domain.MealItem;
import com.example.mealTracker.domain.TodaySummary;

import java.util.List;

public record MealMessageResponse(
        String assistantText,
        TodaySummary todaySummary,
        List<MealItem> items,
        NeedConfirm needConfirm
) {
    public static MealMessageResponse normal(
            String assistantText,
            TodaySummary summary,
            List<MealItem> items
    ) {
        return new MealMessageResponse(assistantText, summary, items, null);
    }

    public static MealMessageResponse needConfirm(
            String message,
            String rawName,
            int count,
            List<FoodMaster> suggestions,
            TodaySummary summary,
            List<MealItem> items
    ) {
        return new MealMessageResponse(
                message,
                summary,
                items,
                NeedConfirm.from(rawName, count, suggestions)
        );
    }
}