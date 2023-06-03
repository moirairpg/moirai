package es.thalesalv.chatrpg.adapters.rest.client;

import es.thalesalv.chatrpg.domain.model.EventData;
import es.thalesalv.chatrpg.domain.model.openai.completion.CompletionResponse;
import reactor.core.publisher.Mono;

public interface CompletionApiService<T> {

    Mono<CompletionResponse> callCompletion(final T request, final EventData eventData);
}
