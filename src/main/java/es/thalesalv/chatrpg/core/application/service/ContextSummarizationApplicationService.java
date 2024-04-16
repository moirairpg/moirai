package es.thalesalv.chatrpg.core.application.service;

import java.util.Map;

import es.thalesalv.chatrpg.core.domain.channelconfig.ModelConfiguration;
import reactor.core.publisher.Mono;

public interface ContextSummarizationApplicationService {

    Mono<Map<String, Object>> summarizeWith(String channelId, String messageId, String botName,
            ModelConfiguration modelConfiguration);
}
