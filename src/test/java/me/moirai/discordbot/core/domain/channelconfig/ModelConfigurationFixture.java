package me.moirai.discordbot.core.domain.channelconfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelConfigurationFixture {

    public static ModelConfiguration.Builder gpt3516k() {

        ModelConfiguration.Builder modelConfigurationBuilder = ModelConfiguration.builder();
        modelConfigurationBuilder.aiModel(ArtificialIntelligenceModel.GPT35_16K);
        modelConfigurationBuilder.frequencyPenalty(0.2);
        modelConfigurationBuilder.presencePenalty(0.2);
        modelConfigurationBuilder.maxTokenLimit(100);
        modelConfigurationBuilder.temperature(1.0);

        Map<String, Double> logitBias = new HashMap<>();
        logitBias.put("ABC", 50.0);
        logitBias.put("DEF", 5.0);

        List<String> stopSequences = new ArrayList<>();
        stopSequences.add("ABC");

        modelConfigurationBuilder.logitBias(logitBias);
        modelConfigurationBuilder.stopSequences(stopSequences);

        return modelConfigurationBuilder;
    }

    public static ModelConfiguration.Builder gpt4128k() {

        ModelConfiguration.Builder modelConfigurationBuilder = ModelConfiguration.builder();
        modelConfigurationBuilder.aiModel(ArtificialIntelligenceModel.GPT4_128K);
        modelConfigurationBuilder.frequencyPenalty(0.2);
        modelConfigurationBuilder.presencePenalty(0.2);
        modelConfigurationBuilder.maxTokenLimit(100);
        modelConfigurationBuilder.temperature(1.0);

        Map<String, Double> logitBias = new HashMap<>();
        logitBias.put("ABC", 50.0);
        logitBias.put("DEF", 5.0);

        List<String> stopSequences = new ArrayList<>();
        stopSequences.add("ABC");

        modelConfigurationBuilder.logitBias(logitBias);
        modelConfigurationBuilder.stopSequences(stopSequences);

        return modelConfigurationBuilder;
    }
}
