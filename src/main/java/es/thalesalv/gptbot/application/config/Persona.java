package es.thalesalv.gptbot.application.config;

import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Persona {

    private List<String> channelIds;
    private String intent;
    private String personality;
    private String model;
    private int maxTokens;
    private int chatHistoryMemory;
    private double temperature;
    private double frequencyPenalty;
    private double presencePenalty;
    private String moderationAbsolute;
    private Map<String, Double> moderationRules;
}