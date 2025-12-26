package com.example.mealTracker.dto;


import com.example.mealTracker.domain.FoodMaster;

import java.util.List;

public record NeedConfirm (
        String rawName,
        int count,
        List<ConfirmCandidate> suggestions
) {
    public static NeedConfirm from(
            String rawName,
            int count,
            List<FoodMaster> foods
    ) {
        List<ConfirmCandidate> list = foods.stream().
                map(f -> new ConfirmCandidate(f.getName(), f.getProtein())).toList();
        return new NeedConfirm(rawName, count, list);
    }
}


