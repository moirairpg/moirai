package me.moirai.storyengine.infrastructure.outbound.adapter.generation;

import java.util.Map;

import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.client.RestClient;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import me.moirai.storyengine.common.enums.ActionDifficulty;
import me.moirai.storyengine.common.enums.ActionVerdict;
import me.moirai.storyengine.common.enums.ArtificialIntelligenceModel;
import me.moirai.storyengine.common.enums.CharacterAttribute;
import me.moirai.storyengine.common.enums.CharacterSkill;
import me.moirai.storyengine.common.enums.MessageAuthorRole;
import me.moirai.storyengine.common.enums.SignatureSkill;
import me.moirai.storyengine.core.port.outbound.generation.ActionEvaluationPort;
import me.moirai.storyengine.core.port.outbound.generation.ActionEvaluationRequest;
import me.moirai.storyengine.core.port.outbound.generation.ActionEvaluationResult;

@Component
public class ActionEvaluationAdapter implements ActionEvaluationPort {

    private static final double TEMPERATURE = 0.1;
    private static final int MAX_OUTPUT_TOKENS = 300;
    private static final String SCHEMA_TYPE = "json_schema";
    private static final String SCHEMA_NAME = "action_verdict";
    private static final String SCHEMA = """
            {
              "type": "object",
              "properties": {
                "verdict": { "type": "string", "enum": ["NO_CHECK", "CHECK", "IMPOSSIBLE"] },
                "attribute": { "type": ["string", "null"], "enum": ["STRENGTH", "AGILITY", "VIGOR", "INTELLIGENCE", "AWARENESS", "CHARISMA", null] },
                "skill": { "type": ["string", "null"], "enum": ["ATHLETICS", "ACROBATICS", "STEALTH", "ENDURANCE", "LORE", "ALCHEMY", "DESTRUCTION", "RESTORATION", "ILLUSION", "CONJURATION", "ALTERATION", "PERCEPTION", "SURVIVAL", "INTUITION", "PERSUASION", "DECEPTION", "INTIMIDATION", "PERFORMANCE", "INSPIRE", "DEADEYE", "BERSERK", "ZEAL", "SPELLWEAVE", "BACKSTAB", "HEX", "BLESSING", "COMMUNE", null] },
                "difficulty": { "type": ["string", "null"], "enum": ["EASY", "MEDIUM", "HARD", "VERY_HARD", "FORMIDABLE", null] },
                "reason": { "type": "string" }
              },
              "required": ["verdict", "attribute", "skill", "difficulty", "reason"],
              "additionalProperties": false
            }
            """;

    private final String token;
    private final String responsesUri;
    private final RestClient openAiClient;
    private final JsonMapper jsonMapper;
    private final Map<String, Object> schema;

    public ActionEvaluationAdapter(
            @Value("${moirai.openai.api.responses-uri}") String responsesUri,
            @Value("${moirai.openai.api.token}") String token,
            RestClient openAiClient,
            JsonMapper jsonMapper) {

        this.token = token;
        this.responsesUri = responsesUri;
        this.openAiClient = openAiClient;
        this.jsonMapper = jsonMapper;
        this.schema = jsonMapper.readValue(SCHEMA, new TypeReference<Map<String, Object>>() {
        });
    }

    @Override
    public ActionEvaluationResult evaluateAction(ActionEvaluationRequest request) {

        var input = request.messages().stream()
                .map(m -> new OpenAiInputMessage(toApiRole(m.role()), m.content()))
                .toList();

        var apiRequest = OpenAiResponsesApiRequest.builder()
                .model(ArtificialIntelligenceModel.GPT54_MINI.getOfficialModelName())
                .instructions(request.instructions())
                .input(input)
                .temperature(TEMPERATURE)
                .maxOutputTokens(MAX_OUTPUT_TOKENS)
                .text(new OpenAiTextOptions(new OpenAiTextFormat(SCHEMA_TYPE, SCHEMA_NAME, true, schema)))
                .build();

        var response = openAiClient.post()
                .uri(responsesUri)
                .headers(headers -> {
                    headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
                    headers.add(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
                })
                .body(apiRequest)
                .retrieve()
                .body(OpenAiResponsesApiResponse.class);

        return toResult(response);
    }

    private String toApiRole(MessageAuthorRole role) {
        return switch (role) {
            case SYSTEM -> "developer";
            case USER -> "user";
            case ASSISTANT -> "assistant";
        };
    }

    private ActionEvaluationResult toResult(OpenAiResponsesApiResponse response) {

        var outputText = response.getOutput().get(0).getContent().get(0).getText();
        var parsed = jsonMapper.readValue(outputText, ActionEvaluationResponse.class);

        return new ActionEvaluationResult(
                ActionVerdict.valueOf(parsed.getVerdict()),
                EnumUtils.getEnum(CharacterAttribute.class, parsed.getAttribute()),
                validSkillNameOrNull(parsed.getSkill()),
                EnumUtils.getEnum(ActionDifficulty.class, parsed.getDifficulty()),
                parsed.getReason());
    }

    private String validSkillNameOrNull(String skill) {

        if (EnumUtils.isValidEnum(CharacterSkill.class, skill) || EnumUtils.isValidEnum(SignatureSkill.class, skill)) {
            return skill;
        }

        return null;
    }
}
