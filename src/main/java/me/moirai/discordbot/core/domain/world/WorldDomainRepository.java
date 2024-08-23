package me.moirai.discordbot.core.domain.world;

import java.util.Optional;

public interface WorldDomainRepository {

    World save(World world);

    Optional<World> findById(String id);

    void deleteById(String id);
}
