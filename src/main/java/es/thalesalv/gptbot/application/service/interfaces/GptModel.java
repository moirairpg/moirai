package es.thalesalv.gptbot.application.service.interfaces;

import es.thalesalv.gptbot.application.config.Persona;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface GptModel {

    Mono<String> generate(final String prompt, final Persona persona);
}
