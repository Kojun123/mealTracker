package com.example.mealTracker.dto;


import com.example.mealTracker.domain.MealFavorite;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class FavoriteMealResponse {
    private String id;
    private String name;
    private String userId;
    private int protein;
    private int calories;
    private int sortOrder;
    private LocalDateTime createdAt;

    public MealFavorite toEntity() {
        return MealFavorite.builder()
                .id(id)
                .userId(userId)
                .name(name)
                .protein(protein)
                .calories(calories)
                .build();
    }
}
