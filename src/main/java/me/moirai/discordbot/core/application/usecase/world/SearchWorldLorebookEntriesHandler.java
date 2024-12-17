package me.moirai.discordbot.core.application.usecase.world;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.exception.AssetAccessDeniedException;
import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.port.WorldQueryRepository;
import me.moirai.discordbot.core.application.usecase.world.request.SearchWorldLorebookEntries;
import me.moirai.discordbot.core.application.usecase.world.result.SearchWorldLorebookEntriesResult;
import me.moirai.discordbot.core.domain.world.World;
import me.moirai.discordbot.core.domain.world.WorldLorebookEntryRepository;

@UseCaseHandler
public class SearchWorldLorebookEntriesHandler
        extends AbstractUseCaseHandler<SearchWorldLorebookEntries, SearchWorldLorebookEntriesResult> {

    private static final String USER_DOES_NO_PERMISSION = "User does not have permission to view this world";
    private static final String WORLD_NOT_FOUND = "The world where the entries are being search doesn't exist";

    private final WorldQueryRepository worldRepository;
    private final WorldLorebookEntryRepository repository;

    public SearchWorldLorebookEntriesHandler(WorldQueryRepository worldRepository,
            WorldLorebookEntryRepository repository) {

        this.worldRepository = worldRepository;
        this.repository = repository;
    }

    @Override
    public SearchWorldLorebookEntriesResult execute(SearchWorldLorebookEntries query) {

        World world = worldRepository.findById(query.getWorldId())
                .orElseThrow(() -> new AssetNotFoundException(WORLD_NOT_FOUND));

        if (world.canUserRead(query.getRequesterDiscordId())) {
            return repository.search(query);
        }

        throw new AssetAccessDeniedException(USER_DOES_NO_PERMISSION);
    }
}