package es.thalesalv.chatrpg.domain.model.openai.dto;

import java.util.Map;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ModelSettings {

    private String id;
    private String modelName;
    private String stopToken;
    private String owner;
    private int maxTokens;
    private int chatHistoryMemory;
    private double temperature;
    private double frequencyPenalty;
    private double presencePenalty;
    private Map<String, Integer> logitBias;
}
