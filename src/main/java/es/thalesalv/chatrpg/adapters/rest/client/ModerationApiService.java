package es.thalesalv.chatrpg.adapters.rest.client;

import es.thalesalv.chatrpg.domain.model.openai.moderation.ModerationRequest;
import es.thalesalv.chatrpg.domain.model.openai.moderation.ModerationResponse;
import reactor.core.publisher.Mono;

public interface ModerationApiService {

    Mono<ModerationResponse> callModeration(final ModerationRequest request);
}