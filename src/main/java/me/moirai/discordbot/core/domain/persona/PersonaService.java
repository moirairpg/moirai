package me.moirai.discordbot.core.domain.persona;

import me.moirai.discordbot.core.application.usecase.persona.request.CreatePersona;
import me.moirai.discordbot.core.application.usecase.persona.request.DeletePersona;
import me.moirai.discordbot.core.application.usecase.persona.request.GetPersonaById;
import me.moirai.discordbot.core.application.usecase.persona.request.UpdatePersona;
import reactor.core.publisher.Mono;

public interface PersonaService {

    Persona getPersonaById(GetPersonaById query);

    Persona getPersonaById(String id);

    Mono<Persona> createFrom(CreatePersona command);

    Mono<Persona> update(UpdatePersona command);

    void deletePersona(DeletePersona command);
}
