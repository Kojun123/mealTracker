package com.example.mealTracker.dto;

import com.example.mealTracker.domain.MealSession;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MealSessionResponse {

    private Long id;
    private String status;
    private String statusText;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public MealSessionResponse(MealSession session) {
        this.id = session.getId();
        this.status = session.getStatus().name();
        this.statusText = session.getStatus().getDescription();
        this.startDate = session.getStartedAt();
        this.endDate = session.getEndedAt();
    }
}
