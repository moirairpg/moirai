package me.moirai.discordbot.core.application.usecase.adventure;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.exception.AssetAccessDeniedException;
import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.port.AdventureQueryRepository;
import me.moirai.discordbot.core.application.usecase.adventure.request.SearchAdventureLorebookEntries;
import me.moirai.discordbot.core.application.usecase.adventure.result.SearchAdventureLorebookEntriesResult;
import me.moirai.discordbot.core.domain.adventure.Adventure;
import me.moirai.discordbot.core.domain.adventure.AdventureLorebookEntryRepository;

@UseCaseHandler
public class SearchAdventureLorebookEntriesHandler
        extends AbstractUseCaseHandler<SearchAdventureLorebookEntries, SearchAdventureLorebookEntriesResult> {

    private static final String USER_DOES_NO_PERMISSION = "User does not have permission to view this adventure";
    private static final String ADVENTURE_NOT_FOUND = "The adventure where the entries are being search doesn't exist";

    private final AdventureQueryRepository adventureRepository;
    private final AdventureLorebookEntryRepository repository;

    public SearchAdventureLorebookEntriesHandler(AdventureQueryRepository adventureRepository,
            AdventureLorebookEntryRepository repository) {

        this.adventureRepository = adventureRepository;
        this.repository = repository;
    }

    @Override
    public SearchAdventureLorebookEntriesResult execute(SearchAdventureLorebookEntries query) {

        Adventure adventure = adventureRepository.findById(query.getAdventureId())
                .orElseThrow(() -> new AssetNotFoundException(ADVENTURE_NOT_FOUND));

        if (adventure.canUserRead(query.getRequesterDiscordId())) {
            return repository.search(query);
        }

        throw new AssetAccessDeniedException(USER_DOES_NO_PERMISSION);
    }
}
