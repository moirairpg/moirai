package es.thalesalv.gptbot.application.config;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Persona {

    private List<String> channelIds;
    private String intent;
    private String personality;
    private int maxTokens;
    private double temperature;
    private double frequencyPenalty;
    private double presencePenalty;
    private int chatHistoryMemory;
}