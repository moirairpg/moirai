package me.moirai.discordbot.infrastructure.outbound.adapter.request;

import java.util.Map;

public class ModerationConfigurationRequest {

    private final boolean isAbsolute;
    private final Map<String, Double> thresholds;

    private ModerationConfigurationRequest(boolean isAbsolute, Map<String, Double> thresholds) {
        this.isAbsolute = isAbsolute;
        this.thresholds = thresholds;
    }

    public static ModerationConfigurationRequest build(boolean isAbsolute, Map<String, Double> thresholds) {
        return new ModerationConfigurationRequest(isAbsolute, thresholds);
    }

    public boolean isAbsolute() {
        return isAbsolute;
    }

    public Map<String, Double> getThresholds() {
        return thresholds;
    }
}
