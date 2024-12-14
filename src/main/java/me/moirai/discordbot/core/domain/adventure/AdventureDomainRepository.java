package me.moirai.discordbot.core.domain.adventure;

import java.util.Optional;

public interface AdventureDomainRepository {

    Optional<Adventure> findById(String id);

    Adventure save(Adventure adventure);

    void deleteById(String id);
}
