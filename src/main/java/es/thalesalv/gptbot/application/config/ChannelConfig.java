package es.thalesalv.gptbot.application.config;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChannelConfig {

    private List<String> channelIds;
    private String channelPurpose;
    private String channelInstructions;
    private int maxTokens;
    private double temperature;
    private double frequencyPenalty;
    private double presencePenalty;
    private int chatHistoryMemory;
    private Personality personality;
}