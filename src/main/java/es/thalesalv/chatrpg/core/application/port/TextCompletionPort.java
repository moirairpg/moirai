package es.thalesalv.chatrpg.core.application.port;

import es.thalesalv.chatrpg.core.application.model.request.TextGenerationRequest;
import es.thalesalv.chatrpg.core.application.model.result.TextGenerationResult;
import reactor.core.publisher.Mono;

public interface TextCompletionPort {

    Mono<TextGenerationResult> generateTextFrom(TextGenerationRequest request);
}
