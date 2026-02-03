package com.example.mealTracker.domain;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class MealFavorite {
    private String id;
    private String userId; //user_id
    private String name;
    private int protein;
    private int calories;
    private int sortOrder; //sort_order
    private LocalDateTime createdAt; //created_at

    public MealFavorite(String id, String userId, String name, int protein, int calories, int sortOrder, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.protein = protein;
        this.calories = calories;
        this.sortOrder = sortOrder;
        this.createdAt = createdAt;
    }
}
