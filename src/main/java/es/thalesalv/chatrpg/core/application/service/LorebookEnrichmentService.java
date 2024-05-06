package es.thalesalv.chatrpg.core.application.service;

import java.util.Map;

import es.thalesalv.chatrpg.core.domain.channelconfig.ModelConfiguration;
import reactor.core.publisher.Mono;

public interface LorebookEnrichmentService {

    Mono<Map<String, Object>> enrich(String worldId, Map<String, Object> contextWithSummary,
            ModelConfiguration modelConfiguration);
}
