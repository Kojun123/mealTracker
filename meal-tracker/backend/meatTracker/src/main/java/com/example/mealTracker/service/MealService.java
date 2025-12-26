package com.example.mealTracker.service;

import com.example.mealTracker.domain.FoodMaster;
import com.example.mealTracker.domain.MealItem;
import com.example.mealTracker.domain.MealSession;
import com.example.mealTracker.domain.TodaySummary;
import com.example.mealTracker.dto.*;
import com.example.mealTracker.mapper.FoodMasterMapper;
import com.example.mealTracker.mapper.MealItemMapper;
import com.example.mealTracker.mapper.MealSessionMapper;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MealService {

    private static final double GOAL_CAL = 2500;
    private static final double GOAL_PRO = 150;

    private final OpenAiService openAiService;
    private final MealItemMapper mealItemMapper;
    private final MealSessionMapper mealSessionMapper;
    private final FoodMasterMapper foodMasterMapper;
    private final FoodEstimator estimator;

    Logger logger = LoggerFactory.getLogger(this.getClass());


    public Long getSessionId() {
        return mealSessionMapper.findActiveSessionId();
    }

    public MealSession findSessionInfo(Long sessionId) {
        return mealSessionMapper.findSessionInfo(sessionId);
    }

    public List<MealItem> findItemsBySessionId(Long sessionId) {
        return mealItemMapper.findItemsBySessionId(sessionId);
    }

    public MealMessageResponse handle(MealMessageRequest req, Long sessionId) {

        String msg = req.message() == null ? "" : req.message().trim();
        if (msg.isBlank()) {
            return build("빈 입력값입니다.", sessionId);
        }

        JsonNode action = openAiService.parseMealAction(msg);
        String intent = action.path("intent").asText("UNKNOWN");
        JsonNode itemsNode = action.path("items");

        if ("MANUAL_RESET".equals(intent)) {
            // TODO: 실제 reset 로직이 있으면 여기서 수행
            String assistantText = "수동 초기화 요청 처리";
            return build(assistantText, sessionId);
        }

        if ("END_SUMMARY".equals(intent)) {
            String assistantText = "오늘 요약 종료";
            return build(assistantText, sessionId);
        }

        if ("LOG_FOOD".equals(intent)) {

            if (!itemsNode.isArray() || itemsNode.isEmpty()) {
                return build("기록할 음식이 없음", sessionId);
            }

            for (JsonNode it : itemsNode) {

                String rawName = it.path("name").asText();
                int count = it.path("count").asInt(1);
                if (count < 1) count = 1;

                String normalized = normalizeName(rawName);
                TodaySummary summary = calcSummary(sessionId);
                List<MealItem> items = findItemsBySessionId(sessionId);

                FoodMaster exact = foodMasterMapper.findByName(normalized);
                if (exact != null) {
                    continue; // 문제 없음
                }

                List<FoodMaster> suggestions =
                        foodMasterMapper.findSimilarByName(normalized);

                    return MealMessageResponse.needConfirm(
                            "‘" + rawName + "’는 등록된 음식이 아님",
                            rawName,
                            count,
                            suggestions,
                            summary,
                            items
                    );
            }

            int total = 0;
            double addPro = 0;
            int dbHits = 0;
            int estimates = 0;

            for (JsonNode it : itemsNode) {

                String rawName = it.path("name").asText();
                int count = it.path("count").asInt(1);
                if (count < 1) count = 1;

                String assumption = it.path("assumption").asText("");
                String normalized = normalizeName(rawName);

                double calories = 0;
                double protein = 0;
                String source;

                FoodMaster fm = foodMasterMapper.findByName(normalized);


                if (fm != null) {
                    protein = fm.getProtein() * count;
                    calories = fm.getKcal() == null ? 0 : fm.getKcal() * count;
                    source = "DB";
                    dbHits++;
                } else {
                    EstimateResult est = estimator.estimate(normalized, count);
                    protein = est.protein();
                    calories = est.calories();
                    source = "ESTIMATE";
                    estimates++;
                }

                InsertItem(normalized, count, calories, protein, sessionId);

                total++;
                addPro += protein;
            }

            String assistantText =
                    total + "개 기록함. DB " + dbHits + "개, 추정 " + estimates + "개. " +
                            "이번 +" + Math.round(addPro) + "g";

            return build(assistantText, sessionId);
        }
        return build("", sessionId);
    }


    public String normalizeName(String raw) {
        if (raw == null) return "";

        String s = raw.trim();

        // 1. 공백 정리
        s = s.replaceAll("\\s+", " ");

        // 2. 수량/불필요 단어 제거
        s = s.replaceAll("(한개|한 개|1개|두개|2개|세개|3개)", "");
        s = s.replaceAll("(먹음|먹었어|마심|마셨어)", "");

        // 3. 괄호 제거
        s = s.replaceAll("\\(.*?\\)", "");

        s = s.trim();

        // 4. 별칭 교정
        return aliasMap(s);
    }

    private String aliasMap(String s) {
        if (s.equalsIgnoreCase("셀릭스")) return "셀렉스";
        if (s.equalsIgnoreCase("셀릭스 프로틴")) return "셀렉스";
        if (s.equalsIgnoreCase("닭가슴")) return "닭가슴살";
        if (s.equalsIgnoreCase("계란")) return "계란"; // 그대로

        return s;
    }

    private FoodMaster findFoodMasterByCandidates(String rawName, List<String> candidates) {

        // 후보가 null이면 빈 리스트
        List<String> list = candidates == null ? List.of() : candidates;

        // 1) 우선순위: GPT 후보 -> rawName
        java.util.LinkedHashSet<String> pool = new java.util.LinkedHashSet<>();
        for (String c : list) {
            if (c != null && !c.isBlank()) pool.add(c);
        }
        pool.add(rawName);

        for (String c : pool) {
            String key = normalizeName(c);
            if (key.isBlank()) continue;

            FoodMaster fm = foodMasterMapper.findByName(key);
            if (fm != null) return fm;
        }
        return null;
    }






    private void InsertItem(String name, int addCount, double addCal, double addPro, long sessionId) {
       MealItem item = new MealItem(name, addCount, addCal, addPro, sessionId);
       mealItemMapper.insertItem(item);
    }

    public MealMessageResponse build(String assistantText, long sessionId) {
        TodaySummary summary = calcSummary(sessionId);
        return MealMessageResponse.normal(
                assistantText + "\n" + remainText(summary),
                summary,
                List.copyOf(mealItemMapper.findItemsBySessionId(sessionId))
        );
    }

    public TodaySummary calcSummary(long sessionId) {
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

        return "\n" +
                "남은 단백질 " + Math.round(remainPro) + "/" + Math.round(s.getGoalProtein())
                + "\n" +
                "남은 칼로리 " + Math.round(remainCal) + "/" + Math.round(s.getGoalCalories());
    }

    public TodayResponse getToday(Long sessionId) {

        if (sessionId == null) {
            return TodayResponse.empty();
        }

        TodaySummary summary = calcSummary(sessionId);
        MealSession session = findSessionInfo(sessionId);
        List<MealItem> items = findItemsBySessionId(sessionId);

        return TodayResponse.of(session, summary, items);
    }


}

