package me.moirai.discordbot.core.domain.persona;

import java.util.Optional;

public interface PersonaDomainRepository {

    Persona save(Persona persona);

    void deleteById(String id);

    Optional<Persona> findById(String id);
}