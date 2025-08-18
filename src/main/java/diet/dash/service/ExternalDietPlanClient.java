package diet.dash.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import diet.dash.model.DietPlan;
import diet.dash.model.DietPlanRequest;
import diet.dash.config.OpenAIConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalDietPlanClient {

    @Qualifier("openAiWebClient")
    private final WebClient openAiWebClient;

    private final OpenAIConfig openAIConfig;

    public DietPlan callOpenAI(DietPlanRequest request) {
        String prompt = buildPrompt(request);

        // Ровно тот payload, который ты просил
        Map<String, Object> payload = Map.of(
                "model", openAIConfig.getModel(),
                "input", prompt,
                "top_p", 1,
                "tool_choice", "none",
                "service_tier", "priority",
                "reasoning", Map.of("effort", "low")
        );

        System.out.println("Request payload: " + payload);

        String raw;
        try {
            System.out.println("Making request to: " + openAIConfig.getBaseUrl() + "/responses");
            System.out.println("Using model: " + openAIConfig.getModel());
            
            raw = openAiWebClient.post()
                    .uri("/responses")
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            System.out.println("Response received: " + raw);
        } catch (Exception e) {
            System.err.println("Error calling OpenAI API: " + e.getMessage());
            System.err.println("Full error: " + e);
            throw e;
        }

        String json = extractJsonFromResponses(raw);
        System.out.println(json);
        return deserializeDietPlan(json);
    }

    private String buildPrompt(DietPlanRequest req) {
        String safeComments = sanitizeComments(req.getComments());

        String targetBlock = getString(req);

        // ТВОЙ ПОЛНЫЙ ИНПУТ (структура + правила), но с подстановкой переменных из request
        return """
            Create a 1-day meal plan based on the input below.
            Return the answer strictly as valid JSON, with no text outside and no comments inside.

            JSON structure:

            {
              "dailyCalories": number,
              "macros": { "protein": number, "carbs": number, "fat": number },
              "meals": [
                { "name": string, "items": [
                  { "product": string, "amount": string, "calories": number, "protein": number, "carbs": number, "fat": number }
                ] }
              ],
              "notes": string,
              "estimatedCostEUR": number
            }

            Rules:

            dailyCalories and macros must match the target values within ±5%%.

            Distribute calories and macros evenly across meals, with extra emphasis on protein in the morning and post-workout.

            Use simple, widely available ingredients.

            estimatedCostEUR must be the approximate cost of ingredients in a typical European supermarket.

            Apply user comments only if they are clearly relevant to diet preferences, restrictions, or allergies (e.g., “no fish”, “vegetarian”, “no dairy”).

            Ignore irrelevant or nonsensical comments.

            Input data:
            Sex: %s; Age: %d; Height: %d cm; Weight: %d kg.
            Weight goal: %s.
            %s
            Meals per day: %d.
            Comments: "%s".
            """.formatted(
                nullToUnknown(req.getGender()),
                req.getAge(),
                req.getHeight_cm(),
                req.getWeight_kg(),
                nullToUnknown(req.getWeightGoal()),
                targetBlock,
                req.getMealsCount(),
                safeComments
        );
    }

    private static @NotNull String getString(DietPlanRequest req) {
        String targetBlock;
        if (req.getTarget() != null) {
            targetBlock = String.format(
                    "Target calories: %d kcal.\nTarget macros: protein %d g, carbs %d g, fat %d g.\n",
                    req.getTarget().getCalories(),
                    req.getTarget().getProtein_g(),
                    req.getTarget().getCarbs_g(),
                    req.getTarget().getFat_g()
            );
        } else {
            targetBlock = "Target calories/macros: infer consistent values for the stated weightGoal.\n";
        }
        return targetBlock;
    }

    private String sanitizeComments(String comments) {
        if (comments == null) return "";
        String trimmed = comments.replaceAll("\\p{Cntrl}", "").trim();
        if (trimmed.length() > 300) trimmed = trimmed.substring(0, 300);
        return trimmed;
    }

    private String nullToUnknown(String v) {
        return (v == null || v.isBlank()) ? "unknown" : v;
    }

    /**
     * Парсер под Responses API:
     * ожидаем, что JSON лежит в output[].content[].text
     * Собираем все text куски в один строковый JSON.
     */
    private String extractJsonFromResponses(String raw) {
        try {
            ObjectMapper om = new ObjectMapper();
            JsonNode root = om.readTree(raw);

            JsonNode outputArr = root.path("output");
            if (!outputArr.isArray() || outputArr.isEmpty()) {
                throw new IllegalStateException("OpenAI response: output array is empty or missing");
            }

            StringBuilder sb = new StringBuilder();
            for (JsonNode out : outputArr) {
                JsonNode contentArr = out.path("content");
                if (contentArr.isArray()) {
                    for (JsonNode c : contentArr) {
                        // типично: { "type": "output_text", "text": "..." }
                        JsonNode textNode = c.path("text");
                        if (textNode.isTextual()) {
                            sb.append(textNode.asText());
                        }
                    }
                }
            }
            String result = sb.toString().trim();
            if (result.isEmpty()) {
                // fallback: иногда бывает "text" прямо на верхнем уровне элемента
                for (JsonNode out : outputArr) {
                    JsonNode maybeText = out.path("text");
                    if (maybeText.isTextual()) {
                        result = maybeText.asText().trim();
                        if (!result.isEmpty()) break;
                    }
                }
            }
            if (result.isEmpty()) {
                throw new IllegalStateException("OpenAI response: no output.content.text found");
            }
            return result;
        } catch (Exception e) {
            log.error("Failed to parse Responses API payload:\n{}", raw, e);
            throw new RuntimeException("Failed to parse OpenAI Responses API response", e);
        }
    }

    private DietPlan deserializeDietPlan(String json) {
        try {
            ObjectMapper om = new ObjectMapper();
            return om.readValue(json, DietPlan.class);
        } catch (Exception e) {
            log.error("Failed to deserialize DietPlan JSON:\n{}", json, e);
            throw new RuntimeException("Invalid JSON returned from model", e);
        }
    }
}
