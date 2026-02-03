package com.example.mealTracker.dto;


import com.example.mealTracker.domain.MealFavorite;
import com.example.mealTracker.domain.MealItem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FavoriteMealRequest
{
    private String id; //uuid
    private String name; //food name
    private int protein;
    private int calories;
    private String userId;


    public FavoriteMealRequest(String id, String name, int protein, int calories) {
        this.id = id;
        this.name = name;
        this.protein = protein;
        this.calories = calories;
    }

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
