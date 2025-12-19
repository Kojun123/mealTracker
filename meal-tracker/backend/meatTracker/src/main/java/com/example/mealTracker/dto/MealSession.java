package com.example.mealTracker.dto;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class MealSession {
    private Long id;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private LocalDateTime createdAt;
}
