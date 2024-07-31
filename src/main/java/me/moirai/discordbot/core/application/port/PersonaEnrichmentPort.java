package me.moirai.discordbot.core.application.port;

import java.util.Map;

import me.moirai.discordbot.infrastructure.outbound.adapter.request.ModelConfigurationRequest;
import reactor.core.publisher.Mono;

public interface PersonaEnrichmentPort {

    Mono<Map<String, Object>> enrichContextWithPersona(Map<String, Object> processedContext, String personaId,
            ModelConfigurationRequest modelConfiguration);
}
