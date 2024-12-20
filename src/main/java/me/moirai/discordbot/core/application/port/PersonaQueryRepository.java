package me.moirai.discordbot.core.application.port;

import java.util.Optional;

import me.moirai.discordbot.core.application.usecase.persona.request.SearchPersonas;
import me.moirai.discordbot.core.application.usecase.persona.result.SearchPersonasResult;
import me.moirai.discordbot.core.domain.persona.Persona;

public interface PersonaQueryRepository {

    Optional<Persona> findById(String id);

    SearchPersonasResult search(SearchPersonas request);

    boolean existsById(String id);
}
