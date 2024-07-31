package me.moirai.discordbot.core.application.port;

import java.util.Map;

import me.moirai.discordbot.infrastructure.outbound.adapter.request.ModelConfigurationRequest;
import reactor.core.publisher.Mono;

public interface StorySummarizationPort {

    Mono<Map<String, Object>> summarizeContextWith(Map<String, Object> context,
            ModelConfigurationRequest modelConfiguration);
}
