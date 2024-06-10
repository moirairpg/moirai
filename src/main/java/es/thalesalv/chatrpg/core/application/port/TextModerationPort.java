package es.thalesalv.chatrpg.core.application.port;

import es.thalesalv.chatrpg.core.application.model.result.TextModerationResult;
import reactor.core.publisher.Mono;

public interface TextModerationPort {

    Mono<TextModerationResult> moderate(String text);
}
