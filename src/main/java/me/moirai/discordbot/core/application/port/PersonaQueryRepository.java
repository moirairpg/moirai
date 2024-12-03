package me.moirai.discordbot.core.application.port;

import java.util.Optional;

import me.moirai.discordbot.core.application.usecase.persona.request.SearchFavoritePersonas;
import me.moirai.discordbot.core.application.usecase.persona.request.SearchPersonasWithReadAccess;
import me.moirai.discordbot.core.application.usecase.persona.request.SearchPersonasWithWriteAccess;
import me.moirai.discordbot.core.application.usecase.persona.result.SearchPersonasResult;
import me.moirai.discordbot.core.domain.persona.Persona;

public interface PersonaQueryRepository {

    Optional<Persona> findById(String id);

    SearchPersonasResult search(SearchPersonasWithReadAccess request);

    SearchPersonasResult search(SearchPersonasWithWriteAccess request);

    SearchPersonasResult search(SearchFavoritePersonas request);

    boolean existsById(String id);
}
