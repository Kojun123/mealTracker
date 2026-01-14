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
                            "assistantText", Map.of("type", "string","minLength", 1),
                            "items", Map.of(
                                    "type", "array",
                                    "items", Map.of(
                                            "type", "object",
                                            "additionalProperties", false,
                                            "properties", Map.of(
                                                    "name", Map.of("type", "string"),
                                                    "count", Map.of("type", "integer", "minimum", 1),

                                                    // 모호한지 여부
                                                    "note", Map.of("type", "boolean"),

                                                    // 필수: 모델 해석 근거/모호점
                                                    "assumption", Map.of("type", "string"),
                                                    "candidates", Map.of("type", "array",
                                                            "items", Map.of("type", "string"),
                                                            "maxItems", 5
                                                    ),
                                                    "calories", Map.of("type", "number", "minimum", 0),
                                                    "protein", Map.of("type", "number", "minimum", 0)

                                            ),
                                            "required", List.of("name", "count", "note", "assumption", "candidates", "calories", "protein")
                                    )
                            )
                    ),
                    "required", List.of("assistantText", "items")
            );

            Map<String, Object> format = Map.of(
                    "type", "json_schema",
                    "name", "meal_action",
                    "strict", true,
                    "schema", schema
            );

            Map<String, Object> body = Map.of(
                    "model", model,
                    "input", List.of(
                            Map.of("role", "system", "content",
                                    "너는 사용자의 음식 입력을 구조화해서 JSON으로 파싱한다." +
                                            "규칙" +
                                            "1) items는 여러 개일 수 있다. 사용자가 한 문장에 여러 음식을 말하면 items에 모두 넣어라." +
                                            "2) count가 없으면 1로 한다." +
                                            "3) calories와 protein은 항상 count=1 기준(1개/1회 제공량 기준) 값만 반환한다." +
                                            "   총합 계산은 서버에서 하므로 절대 총합을 반환하지 마라." +
                                            "4) 음식이 일반 음식(예: 마라탕, 김밥)처럼 평균값만 가능한 경우 calories/protein은 평균 추정값을 넣어라." +
                                            "5) 제품명/규격이 불명확해서 추정이면 note=true로 하고 assumption에 아래 형식으로 질문을 작성한다." +
                                            "   형식: \"제품명 모호, {입력값}={추정되는음식}로 추정됨, 평균 {추정되는음식} 기준 단백질 {n}g, 칼로리 {n}kcal로 등록할까요?\"" +
                                            "6) 추정이 아닌 경우(note=false) assumption은 짧게 근거만 쓴다. 예: \"제품명 명확으로 판단되어 일반적인 기준값 적용\"" +
                                            "7) candidates는 name과 같은 의미의 표기 변형만 0~5개 넣어라(오타/띄어쓰기/표기 차이)." +
                                            "   다른 제품 후보는 candidates에 넣지 마라. 없으면 []." +
                                            "assistantText는 1~2줄로 짧게 요약한다."
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
