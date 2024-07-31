package me.moirai.discordbot.infrastructure.outbound.adapter.request;

import java.util.HashMap;
import java.util.Map;

public class ModerationConfigurationRequestFixture {

    public static ModerationConfigurationRequest absolute() {

        Map<String, Double> thresholds = new HashMap<>();
        thresholds.put("violence", 10.0);

        return ModerationConfigurationRequest.build(true, thresholds);
    }
}
