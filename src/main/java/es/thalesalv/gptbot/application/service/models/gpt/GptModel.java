package es.thalesalv.gptbot.application.service.models.gpt;

import reactor.core.publisher.Mono;

@FunctionalInterface
public interface GptModel {

    Mono<String> generate(final String prompt);
}
