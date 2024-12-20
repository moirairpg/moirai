package me.moirai.discordbot.core.application.port;

import java.util.Optional;

import me.moirai.discordbot.core.application.usecase.world.request.SearchWorlds;
import me.moirai.discordbot.core.application.usecase.world.result.SearchWorldsResult;
import me.moirai.discordbot.core.domain.world.World;

public interface WorldQueryRepository {

    Optional<World> findById(String id);

    SearchWorldsResult search(SearchWorlds request);
}
