package com.example.mealTracker.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class MealItem {
    private Long id;
    private Long sessionId;
    private String name;
    private Integer count;
    private Double calories;
    private Double protein;
    private String assumption;
    private LocalDateTime createdAt;


    public MealItem(String name, int count, double calories, double protein, Long sessionId) {
        this.name = name;
        this.count = count;
        this.calories = calories;
        this.protein = protein;
        this.sessionId = sessionId;
    }
}
