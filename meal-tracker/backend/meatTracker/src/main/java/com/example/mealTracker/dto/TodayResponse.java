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

    public TodayResponse(MealSessionResponse mealSessionResponse, TodaySummary summary, List<MealItem> items) {
        this.session = mealSessionResponse;
        this.summary = summary;
        this.items = items;
    }

    public static TodayResponse of(MealSession session, TodaySummary summary, List<MealItem> items) {
        return new TodayResponse(
                new MealSessionResponse(session),
                summary,
                items
        );
    }

   public static TodayResponse empty() {
       return new TodayResponse(null, null, List.of());
   }

}
