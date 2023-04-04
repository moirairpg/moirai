package es.thalesalv.chatrpg.domain.model.chconf;

import java.util.List;
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
    private String owner;
    private int maxTokens;
    private int chatHistoryMemory;
    private double temperature;
    private double frequencyPenalty;
    private double presencePenalty;
    private List<String> stopSequence;
    private Map<String, Double> logitBias;

    public static ModelSettings defaultModelSettings() {

        return ModelSettings.builder()
                .id("0")
                .build();
    }
}
