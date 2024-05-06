package es.thalesalv.chatrpg.core.application.service;

import java.util.Map;

import es.thalesalv.chatrpg.core.domain.channelconfig.ModelConfiguration;
import reactor.core.publisher.Mono;

public interface PersonaEnrichmentService {

    Mono<Map<String, Object>> enrich(String personaId, Map<String, Object> processedContext,
            ModelConfiguration modelConfiguration);
}
