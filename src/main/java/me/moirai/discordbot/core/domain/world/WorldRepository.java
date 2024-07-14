package me.moirai.discordbot.core.domain.world;

import java.util.Optional;

import me.moirai.discordbot.core.application.usecase.world.request.SearchWorldsWithReadAccess;
import me.moirai.discordbot.core.application.usecase.world.request.SearchWorldsWithWriteAccess;
import me.moirai.discordbot.core.application.usecase.world.result.SearchWorldsResult;

public interface WorldRepository {

    World save(World world);

    Optional<World> findById(String id);

    void deleteById(String id);

    SearchWorldsResult searchWorldsWithReadAccess(SearchWorldsWithReadAccess query);

    SearchWorldsResult searchWorldsWithWriteAccess(SearchWorldsWithWriteAccess query);
}
