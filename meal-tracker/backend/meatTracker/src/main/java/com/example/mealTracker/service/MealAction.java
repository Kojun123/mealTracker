package com.example.mealTracker.service;


import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

public class MealAction {

    public final String intent;
    public final String assistantText;
    public final List<ActionItem> items;

    public MealAction(String intent, String assistantText, List<ActionItem> items) {
        this.intent = intent;
        this.assistantText = assistantText;
        this.items = items;
    }

    public static MealAction unknown(String assistantText) {
        return new MealAction("UNKNOWN", assistantText, List.of());
    }

    public static MealAction from(String intent, String assistantText, JsonNode itemsNode) {
        List<ActionItem> items = new ArrayList<>();
        if (itemsNode != null && itemsNode.isArray()) {
            for (JsonNode it : itemsNode) {
                String name = it.path("name").asText("").trim();
                int count = it.path("count").asInt(1);
                if (!name.isEmpty()) items.add(new ActionItem(name, count));
            }
        }
        return new MealAction(intent, assistantText, items);
    }

    public record ActionItem(String name, int count) {}
}
