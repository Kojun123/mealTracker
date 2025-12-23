package com.example.mealTracker.mapper;

import com.example.mealTracker.domain.MealSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MealSessionMapper {
    Long findActiveSessionId();
    int insertSession(MealSession session);
    int endSession(@Param("id") Long id);
    MealSession findSessionInfo(@Param("sessionId") Long sessionId);
}
