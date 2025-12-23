package com.example.mealTracker.domain;

public enum MealSessionStatus {

    ACTIVE("기록 중"),
    PAUSED("기록 중단"),
    CLOSED("기록 종료");

    private final String description;

    MealSessionStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
    
}
