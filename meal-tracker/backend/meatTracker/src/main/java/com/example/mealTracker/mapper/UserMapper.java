package com.example.mealTracker.mapper;

import com.example.mealTracker.domain.MealTrackerUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    MealTrackerUser findByEmail(@Param("email") String email);

    void insert(MealTrackerUser user);

    int updateTargets(@Param("userId") String userId,
                      @Param("targetCalories") int targetCalories,
                      @Param("targetProtein") int targetProtein);
}
