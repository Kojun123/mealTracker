package com.example.mealTracker.mapper;

import com.example.mealTracker.dto.MealSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MealSessionMapper {
    Long findActiveSessionId();
    int insertSession(MealSession session);
    int endSession(@Param("id") Long id);
}
