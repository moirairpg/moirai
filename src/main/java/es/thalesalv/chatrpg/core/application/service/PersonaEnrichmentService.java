package es.thalesalv.chatrpg.core.application.service;

import java.util.Map;

import es.thalesalv.chatrpg.core.domain.channelconfig.ModelConfiguration;
import reactor.core.publisher.Mono;

public interface PersonaEnrichmentService {

    Mono<Map<String, Object>> enrichContextWith(Map<String, Object> processedContext, String personaId,
            ModelConfiguration modelConfiguration);
}
