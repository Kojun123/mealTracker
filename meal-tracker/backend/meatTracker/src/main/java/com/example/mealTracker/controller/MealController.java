package com.example.mealTracker.controller;


import com.example.mealTracker.dto.MealMessageRequest;
import com.example.mealTracker.dto.MealMessageResponse;
import com.example.mealTracker.mapper.MealSessionMapper;
import com.example.mealTracker.service.MealService;
import com.example.mealTracker.service.OpenAiService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/meal")
public class MealController {

    private final MealService mealService;
    private final MealSessionMapper sessionMapper;

    public MealController(MealService mealService, MealSessionMapper sessionMapper) {
        this.mealService = mealService;
        this.sessionMapper = sessionMapper;
    }

    @PostMapping("/message")
    public MealMessageResponse message(@RequestBody MealMessageRequest req) {
        Long sessionId = sessionMapper.findActiveSessionId();
        return mealService.handle(req, sessionId);
    }
}
