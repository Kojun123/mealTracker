package com.example.mealTracker.dto;

import com.example.mealTracker.domain.MealItem;
import com.example.mealTracker.domain.MealSession;
import com.example.mealTracker.domain.TodaySummary;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TodayResponse {

    private MealSessionResponse session;
    private TodaySummary summary;
    private List<MealItem> items;

    @Builder
    public TodayResponse(MealSessionResponse session, TodaySummary summary, List<MealItem> items) {
        this.session = session;
        this.summary = summary;
        this.items = items;
    }

}
