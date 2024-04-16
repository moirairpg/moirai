package es.thalesalv.chatrpg.core.application.service;

import java.util.Map;

import es.thalesalv.chatrpg.core.domain.channelconfig.ModelConfiguration;
import reactor.core.publisher.Mono;

public interface PersonaEnrichmentApplicationService {

    Mono<Map<String, Object>> enrich(String personaId, String botName, Map<String, Object> processedContext,
            ModelConfiguration modelConfiguration);
}
