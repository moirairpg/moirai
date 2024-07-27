package me.moirai.discordbot.core.application.service;

import java.util.Map;

import me.moirai.discordbot.core.domain.channelconfig.ModelConfiguration;
import reactor.core.publisher.Mono;

public interface PersonaEnrichmentService {

    Mono<Map<String, Object>> enrichContextWithPersona(Map<String, Object> processedContext, String personaId,
            ModelConfiguration modelConfiguration);
}
