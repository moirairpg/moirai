package es.thalesalv.chatrpg.core.application.service;

import java.util.List;
import java.util.Map;

import es.thalesalv.chatrpg.core.domain.channelconfig.ModelConfiguration;
import es.thalesalv.chatrpg.infrastructure.outbound.adapter.response.ChatMessageData;
import reactor.core.publisher.Mono;

public interface StorySummarizationService {

    Mono<Map<String, Object>> summarizeContextWith(List<ChatMessageData> messagesExtracted,
            ModelConfiguration modelConfiguration);
}
