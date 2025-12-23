package com.example.mealTracker.service;

import com.example.mealTracker.domain.MealSessionStatus;
import com.example.mealTracker.mapper.MealSessionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final MealSessionMapper mealSessionMapper;

    public void updateSessionStatus(Long sessionId, MealSessionStatus status) {
        mealSessionMapper.updateSessionStatus(sessionId, status);
    }

}
