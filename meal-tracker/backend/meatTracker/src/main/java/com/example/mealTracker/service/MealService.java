package com.example.mealTracker.service;

import com.example.mealTracker.dto.MealSession;
import com.example.mealTracker.dto.*;
import com.example.mealTracker.mapper.MealItemMapper;
import com.example.mealTracker.mapper.MealSessionMapper;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class MealService {

    private static final double GOAL_CAL = 2500;
    private static final double GOAL_PRO = 150;

    private final OpenAiService openAiService;
    private final MealItemMapper mealItemMapper;
    private final MealSessionMapper mealSessionMapper;

    private final Clock clock = Clock.system(ZoneId.of("Asia/Seoul"));

    public MealService(OpenAiService openAiService, MealItemMapper mealItemMapper, MealSessionMapper mealSessionMapper) {
        this.openAiService = openAiService;
        this.mealItemMapper = mealItemMapper;
        this.mealSessionMapper = mealSessionMapper;
    }

    public MealMessageResponse handle(MealMessageRequest req, Long sessionId) {
        rolloverIfNeeded();

        String msg = req.message() == null ? "" : req.message().trim();
        if (msg.isBlank()) {
            return build("빈 입력은 처리 안 함", sessionId);
        }

        JsonNode action = openAiService.parseMealAction(msg);

        String intent = action.path("intent").asText("UNKNOWN");
        String assistantText = action.path("assistantText").asText("처리 실패");
        JsonNode itemsNode = action.path("items");

        if ("MANUAL_RESET".equals(intent)) {
            return build(assistantText, sessionId);
        }

        if ("END_SUMMARY".equals(intent)) {
            return build(assistantText, sessionId);
        }

        if ("LOG_FOOD".equals(intent)) {
            if (itemsNode.isArray()) {
                for (JsonNode it : itemsNode) {
                    String name = it.path("name").asText();
                    int count = it.path("count").asInt(1);
                    double calories = it.path("calories").asDouble(0);
                    double protein = it.path("protein").asDouble(0);

                    upsertTotal(name, count, calories, protein, sessionId);
                }
                return build(assistantText, sessionId);
                }
        }

        return build(assistantText, sessionId);
    }

    private void rolloverIfNeeded() {
        LocalDate today = LocalDate.now(clock);
    }

    private void upsertTotal(String name, int addCount, double addCal, double addPro, long sessionId) {
        List<MealItem> items = mealItemMapper.findItemsBySessionId(sessionId);
        for (int i = 0; i < items.size(); i++) {
            MealItem it = items.get(i);
            if (it.getName().equals(name)) {
                items.set(i, new MealItem(
                        name,
                        it.getCount() + addCount,
                        it.getCalories() + addCal,
                        it.getProtein() + addPro
                ));
                return;
            }
        }
        items.add(new MealItem(name, addCount, addCal, addPro));
    }


    private MealMessageResponse build(String assistantText, long sessionId) {
        TodaySummary summary = calcSummary(sessionId);
        return new MealMessageResponse(
                assistantText + "\n" + remainText(summary),
                summary,
                List.copyOf(mealItemMapper.findItemsBySessionId(sessionId))
        );
    }

    private TodaySummary calcSummary(long sessionId) {
        double totalCal = 0;
        double totalPro = 0;

        for (MealItem it : mealItemMapper.findItemsBySessionId(sessionId)) {
            totalCal += it.getCalories();
            totalPro += it.getProtein();
        }

        return new TodaySummary(totalCal, totalPro, GOAL_CAL, GOAL_PRO);
    }

    private String remainText(TodaySummary s) {
        double remainPro = Math.max(0, s.getGoalProtein() - s.getTotalProtein());
        double remainCal = Math.max(0, s.getGoalCalories() - s.getTotalCalories());

        return "남은 단백질 " + Math.round(remainPro) + "/" + Math.round(s.getGoalProtein())
                + "\n" +
                "남은 칼로리 " + Math.round(remainCal) + "/" + Math.round(s.getGoalCalories());
    }

}

