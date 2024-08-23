package me.moirai.discordbot.core.domain.persona;

import me.moirai.discordbot.core.application.usecase.persona.request.CreatePersona;
import me.moirai.discordbot.core.application.usecase.persona.request.DeletePersona;
import reactor.core.publisher.Mono;

public interface PersonaService {

    Persona getById(String id);

    Mono<Persona> createFrom(CreatePersona command);

    void delete(DeletePersona command);
}
