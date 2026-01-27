package com.example.mealTracker.controller;


import com.example.mealTracker.dto.*;
import com.example.mealTracker.service.MealService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;

@RestController
@RequestMapping("/api/meal")
public class MealController {

    private final MealService mealService;

    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    @PostMapping("/item")
    public ResponseEntity<MealMessageResponse> insertItem(@RequestBody MealMessageRequest vo, Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        return ResponseEntity.ok(mealService.handle(vo, userId));
    }

    @PutMapping("/item/{itemId}")
    public ResponseEntity<Void> updateItem(Authentication authentication, @RequestBody UpdateItemRequest vo) {
        String userId = (String) authentication.getPrincipal();
        vo.setUserId(userId);
        mealService. updateItem(vo);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<Void> deleteItem(Authentication authentication, @PathVariable Long itemId) {
        String userId = (String) authentication.getPrincipal();
        mealService.deleteItem(userId, itemId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/today")
    public ResponseEntity<TodayResponse> today(Authentication authentication, @RequestParam(required = false) LocalDate date) {
        String userId = (String) authentication.getPrincipal();
        if (date == null) date = LocalDate.now(ZoneId.of("Asia/Seoul"));
        return ResponseEntity.ok(mealService.getToday(userId, date));
    }

    @PostMapping("/setLogs")
    public ResponseEntity<Void> setLogs(Authentication authentication, @RequestBody MealLogRequest vo) {
        String userId = (String) authentication.getPrincipal();
        vo.setEmail(userId);
        mealService.insertLog(vo);
        return ResponseEntity.ok().build();
    }


}
