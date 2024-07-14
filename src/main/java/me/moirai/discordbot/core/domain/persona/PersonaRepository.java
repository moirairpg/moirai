package me.moirai.discordbot.core.domain.persona;

import java.util.Optional;

import me.moirai.discordbot.core.application.usecase.persona.request.SearchPersonasWithReadAccess;
import me.moirai.discordbot.core.application.usecase.persona.request.SearchPersonasWithWriteAccess;
import me.moirai.discordbot.core.application.usecase.persona.result.SearchPersonasResult;

public interface PersonaRepository {

    Optional<Persona> findById(String id);

    Persona save(Persona persona);

    void deleteById(String id);

    SearchPersonasResult searchPersonasWithReadAccess(SearchPersonasWithReadAccess query);

    SearchPersonasResult searchPersonasWithWriteAccess(SearchPersonasWithWriteAccess query);
}