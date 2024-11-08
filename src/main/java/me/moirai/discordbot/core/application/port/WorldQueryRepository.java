package me.moirai.discordbot.core.application.port;

import java.util.Optional;

import me.moirai.discordbot.core.application.usecase.world.request.SearchWorldsWithReadAccess;
import me.moirai.discordbot.core.application.usecase.world.request.SearchWorldsWithWriteAccess;
import me.moirai.discordbot.core.application.usecase.world.result.SearchWorldsResult;
import me.moirai.discordbot.core.domain.world.World;

public interface WorldQueryRepository {

    Optional<World> findById(String id);

    SearchWorldsResult searchWorldsWithReadAccess(SearchWorldsWithReadAccess query);

    SearchWorldsResult searchWorldsWithWriteAccess(SearchWorldsWithWriteAccess query);
}
