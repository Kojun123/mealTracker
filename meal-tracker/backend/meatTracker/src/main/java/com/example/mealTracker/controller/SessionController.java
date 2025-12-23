package com.example.mealTracker.controller;


import com.example.mealTracker.domain.MealSessionStatus;
import com.example.mealTracker.service.MealService;
import com.example.mealTracker.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/meal/session")
@RequiredArgsConstructor
public class SessionController {

    private final MealService mealService;
    private final SessionService sessionService;

    //세션 상태 업데이트 (기록 중)
      @PostMapping("/start")
       public ResponseEntity<Object> startSession() {
          Long sessionId = mealService.getSessionId();
          sessionService.updateSessionStatus(sessionId, MealSessionStatus.ACTIVE);
          return ResponseEntity.ok().build();
      }

    //세션 상태 업데이트 (기록 종료)
      @PostMapping("/end")
       public ResponseEntity<Object> endSession() {
          Long sessionId = mealService.getSessionId();
          sessionService.updateSessionStatus(sessionId, MealSessionStatus.CLOSED);
          return ResponseEntity.ok().build();
      }

    //세션 상태 업데이트 (기록 재개)
      @PostMapping("/pause")
       public ResponseEntity<Object> pauseSession() {
          Long sessionId = mealService.getSessionId();
          sessionService.updateSessionStatus(sessionId, MealSessionStatus.PAUSED);
          return ResponseEntity.ok().build();
      }


}
