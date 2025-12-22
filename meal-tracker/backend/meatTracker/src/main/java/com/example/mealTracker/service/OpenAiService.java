package com.example.mealTracker.service;

import com.example.mealTracker.dto.MealItem;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

import java.util.ArrayList;
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
                                    "enum", List.of("LOG_FOOD", "MANUAL_RESET", "END_SUMMARY", "UNKNOWN")
                            ),
                            "assistantText", Map.of("type", "string"),
                            "items", Map.of(
                                    "type", "array",
                                    "items", Map.of(
                                            "type", "object",
                                            "additionalProperties", false,
                                            "properties", Map.of(
                                                    "name", Map.of("type", "string"),
                                                    "count", Map.of("type", "integer", "minimum", 1),
                                                    "calories", Map.of("type", "number", "minimum", 0),
                                                    "protein", Map.of("type", "number", "minimum", 0),
                                                    "assumption", Map.of("type", "string")
                                            ),
                                            "required", List.of("name", "count","calories", "protein", "assumption")
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
                                    "너는 식단 기록 파서다. 반드시 JSON만 반환한다.\n" +
                                            "목표는 '대략적인 평균 영양 추정'이다. 셀릭스 한개 라고 사용자가 입력시 인터넷에서 셀릭스의 맛마다 다르겠지만 대략적인 영양성분을 파악한뒤 대답한다. 이 추정치는 매번 변경되면 안되고 최대한 고정되어야 한다.(사용자가 셀릭스 입력할 때 마다 값이 바뀌면 곤란하다는 뜻)\n" +
                                            "\n" +
                                            "규칙:\n" +
                                            "- 음식별로 일반적인 1인분/1개 기준을 가정해서 calories/protein을 추정한다.\n" +
                                            "- 사용자가 수량을 말하면 count를 반영해서 calories/protein은 '총합'으로 반환한다.\n" +
                                            "- 수량이 없으면 count=1.\n" +
                                            "- 모호하면 items는 비워도 되고 intent=UNKNOWN으로 하고 assistantText에서 질문해라.\n" +
                                            "- assistantText에는 추정 기준을 짧게 포함해라. 예: \"닭가슴살 1개 대략 150g, 단백질 20g 추정.\"\n" +
                                            "assumption에는 값이 없으면 빈 문자열이라도 넣어야 해" +
                                            "calories/protein은 반드시 숫자여야 하고 count는 integer 값이야"
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
