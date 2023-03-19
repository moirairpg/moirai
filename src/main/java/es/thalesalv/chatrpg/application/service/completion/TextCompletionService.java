package es.thalesalv.chatrpg.application.service.completion;

import java.util.List;

import es.thalesalv.chatrpg.domain.model.openai.dto.MessageEventData;
import reactor.core.publisher.Mono;

public interface TextCompletionService {

    Mono<String> generate(final String prompt, final List<String> messages, final MessageEventData messageEventData);
}
