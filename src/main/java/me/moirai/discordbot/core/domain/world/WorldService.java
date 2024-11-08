package me.moirai.discordbot.core.domain.world;

import java.util.List;

import me.moirai.discordbot.core.application.usecase.world.request.CreateWorld;
import me.moirai.discordbot.core.application.usecase.world.request.CreateWorldLorebookEntry;
import me.moirai.discordbot.core.application.usecase.world.request.DeleteWorld;
import me.moirai.discordbot.core.application.usecase.world.request.DeleteWorldLorebookEntry;
import me.moirai.discordbot.core.application.usecase.world.request.GetWorldLorebookEntryById;
import me.moirai.discordbot.core.application.usecase.world.request.UpdateWorldLorebookEntry;
import reactor.core.publisher.Mono;

public interface WorldService {

    World getWorldById(String worldId);

    Mono<World> createFrom(CreateWorld command);

    void deleteWorld(DeleteWorld command);

    WorldLorebookEntry createLorebookEntry(CreateWorldLorebookEntry command);

    WorldLorebookEntry updateLorebookEntry(UpdateWorldLorebookEntry command);

    List<WorldLorebookEntry> findAllLorebookEntriesByRegex(String worldId, String valueToSearch);

    WorldLorebookEntry findLorebookEntryByPlayerDiscordId(String worldId, String playerDiscordId);

    WorldLorebookEntry findLorebookEntryById(GetWorldLorebookEntryById query);

    void deleteLorebookEntry(DeleteWorldLorebookEntry command);
}
