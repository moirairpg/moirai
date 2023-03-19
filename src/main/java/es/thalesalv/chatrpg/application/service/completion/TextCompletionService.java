package es.thalesalv.chatrpg.application.service.completion;

import java.util.List;

import es.thalesalv.chatrpg.domain.model.openai.dto.MessageEventData;
import reactor.core.publisher.Mono;

public class TextCompletionService implements CompletionService {

    @Override
    public Mono<String> generate(String prompt, List<String> messages, MessageEventData messageEventData) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generate'");
    }
}
