package com.example.mealTracker.mapper;

import com.example.mealTracker.dto.MealItem;
import com.example.mealTracker.dto.TodaySummary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MealItemMapper {
    int insertItem(MealItem item);
    List<MealItem> findItemsBySessionId(@Param("sessionId") Long sessionId);
    TodaySummary findSummaryBySessionId(@Param("sessionId") Long sessionId);
    int deleteItemsBySessionId(@Param("sessionId") Long sessionId);
}
