package me.moirai.discordbot.core.application.port;

import java.util.Optional;

import me.moirai.discordbot.core.application.usecase.world.request.SearchFavoriteWorlds;
import me.moirai.discordbot.core.application.usecase.world.request.SearchWorldsWithReadAccess;
import me.moirai.discordbot.core.application.usecase.world.request.SearchWorldsWithWriteAccess;
import me.moirai.discordbot.core.application.usecase.world.result.SearchWorldsResult;
import me.moirai.discordbot.core.domain.world.World;

public interface WorldQueryRepository {

    Optional<World> findById(String id);

    SearchWorldsResult search(SearchWorldsWithReadAccess request);

    SearchWorldsResult search(SearchWorldsWithWriteAccess request);

    SearchWorldsResult search(SearchFavoriteWorlds request);
}
