package me.moirai.storyengine.core.port.outbound.generation;

import java.util.Map;

import me.moirai.storyengine.common.util.Functions;

public record ModerationConfigurationRequest(

        boolean isEnabled,
        boolean isAbsolute,
        Map<String, Double> thresholds) {

    public ModerationConfigurationRequest {
        thresholds = Functions.mapOrDefault(thresholds, Map.of(), Map::copyOf);
    }
}
