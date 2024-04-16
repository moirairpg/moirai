package es.thalesalv.chatrpg.core.application.port;

import es.thalesalv.chatrpg.core.application.model.request.TextGenerationRequest;
import es.thalesalv.chatrpg.core.application.model.request.TextModerationRequest;
import es.thalesalv.chatrpg.core.application.model.result.TextGenerationResult;
import es.thalesalv.chatrpg.core.application.model.result.TextModerationResult;
import reactor.core.publisher.Mono;

public interface OpenAiPort {

    Mono<TextGenerationResult> generateTextFrom(TextGenerationRequest request);

    Mono<TextModerationResult> moderateTextFrom(TextModerationRequest request);
}
