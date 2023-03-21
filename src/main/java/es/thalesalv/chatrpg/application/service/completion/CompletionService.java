package es.thalesalv.chatrpg.application.service.completion;

import java.util.List;

import es.thalesalv.chatrpg.domain.model.openai.dto.EventData;
import reactor.core.publisher.Mono;

public interface CompletionService {

    Mono<String> generate(final List<String> messages, final EventData eventData);
}
