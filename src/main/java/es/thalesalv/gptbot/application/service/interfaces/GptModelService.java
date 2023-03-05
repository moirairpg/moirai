package es.thalesalv.gptbot.application.service.interfaces;

import java.util.List;

import es.thalesalv.gptbot.application.config.Persona;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface GptModelService {

    Mono<String> generate(final String prompt, final Persona persona, final List<String> messages);
}
