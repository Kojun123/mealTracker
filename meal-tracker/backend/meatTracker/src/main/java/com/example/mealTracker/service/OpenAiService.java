package com.example.mealTracker.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

import java.util.Map;

@Service
public class OpenAiService {

    //gpt model(yml에 있음)
    private final String model;
    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    Logger logger = LoggerFactory.getLogger(this.getClass());

    public OpenAiService(
            @Value("${openai.apiKey}") String apiKey,
            @Value("${openai.model}") String model
    ) {
        this.model = model;

        this.webClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public JsonNode parseMealAction(String userText) {
        try {
            Map<String, Object> schema = Map.of(
                    "type", "object",
                    "additionalProperties", false,
                    "properties", Map.of(
                            "intent", Map.of(
                                    "type", "string",
                                    "enum", List.of("LOG_FOOD", "MANUAL_RESET", "END_SUMMARY", "NEED_CONFIRM")
                            ),
                            "assistantText", Map.of("type", "string","minLength", 1),
                            "items", Map.of(
                                    "type", "array",
                                    "items", Map.of(
                                            "type", "object",
                                            "additionalProperties", false,
                                            "properties", Map.of(
                                                    "name", Map.of("type", "string"),
                                                    "count", Map.of("type", "integer", "minimum", 1),
                                                    // 선택: 사용자가 말한 양 표현을 남김
                                                    "note", Map.of("type", "string"),
                                                    // 필수: 모델 해석 근거/모호점
                                                    "assumption", Map.of("type", "string"),
                                                    "candidates", Map.of("type", "array",
                                                            "items", Map.of("type", "string"),
                                                            "maxItems", 5
                                                    )
                                            ),
                                            "required", List.of("name", "count", "note", "assumption", "candidates")
                                    )
                            )
                    ),
                    "required", List.of("intent", "assistantText", "items")
            );

            Map<String, Object> format = Map.of(
                    "type", "json_schema",
                    "name", "meal_action",
                    "strict", true,
                    "schema", schema
            );

            Map<String, Object> body = Map.of(
                    "model", "gpt-4o-mini",
                    "input", List.of(
                            Map.of("role", "system", "content",
                                    "너는 음식 입력을 구조화해서 파싱만 한다.\n" +
                                            "절대 칼로리 단백질 같은 영양 수치를 추정하거나 계산하지 마라.\n" +
                                            "items에는 음식명(name)과 개수(count) 있으면 넣어라.\n" +
                                            "모호하면 assumption에 왜 모호한지 짧게 써라." +
                                            "note가 없으면 빈 문자열(\"\")을 반드시 포함하라\n" +
                                            "items.candidates에는 name과 의미가 같은/비슷한 표기 후보를 1~5개 넣어라.\n" +
                                            "예: \"셀렉스\" -> [\"셀릭스\",\"셀렉스\"]\n" +
                                            "brand/별칭/줄임말도 포함하되, 영양 수치는 절대 추정하지 마라.\n" +
                                            "candidates가 없으면 빈 배열 [] 을 넣어라"
                            ),
                            Map.of("role", "user", "content", userText)
                    ),
                    "text", Map.of("format", format)
            );

            JsonNode resp = webClient.post()
                    .uri("/responses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            String jsonText = extractOutputText(resp);
            return objectMapper.readTree(jsonText);

        } catch (WebClientResponseException e) {
            logger.error("OpenAI status={} body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            return objectMapper.createObjectNode()
                    .put("intent", "UNKNOWN")
                    .put("assistantText", "GPT 파싱 실패. 입력을 더 구체적으로 써줘")
                    .set("items", objectMapper.createArrayNode());
        }
    }



    private String extractOutputText(JsonNode resp) {
        if (resp == null) return "{}";

        JsonNode output = resp.path("output");
        if (!output.isArray()) return "{}";

        for (JsonNode item : output) {
            if (!"message".equals(item.path("type").asText())) continue;

            JsonNode content = item.path("content");
            if (!content.isArray()) continue;

            for (JsonNode c : content) {
                if ("output_text".equals(c.path("type").asText())) {
                    return c.path("text").asText("{}");
                }
            }
        }
        return "{}";
    }
}
