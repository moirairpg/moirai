package me.moirai.discordbot.core.application.usecase.adventure;

import org.apache.commons.lang3.StringUtils;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.usecase.adventure.request.DeleteAdventureLorebookEntry;
import me.moirai.discordbot.core.domain.adventure.AdventureService;

@UseCaseHandler
public class DeleteAdventureLorebookEntryHandler extends AbstractUseCaseHandler<DeleteAdventureLorebookEntry, Void> {

    private static final String ENTRY_ID_CANNOT_BE_NULL_OR_EMPTY = "Lorebook entry ID cannot be null or empty";
    private static final String ADVENTURE_ID_CANNOT_BE_NULL_OR_EMPTY = "Adventure ID cannot be null or empty";

    private final AdventureService domainService;

    public DeleteAdventureLorebookEntryHandler(AdventureService domainService) {
        this.domainService = domainService;
    }

    @Override
    public void validate(DeleteAdventureLorebookEntry command) {

        if (StringUtils.isBlank(command.getLorebookEntryId())) {
            throw new IllegalArgumentException(ENTRY_ID_CANNOT_BE_NULL_OR_EMPTY);
        }

        if (StringUtils.isBlank(command.getAdventureId())) {
            throw new IllegalArgumentException(ADVENTURE_ID_CANNOT_BE_NULL_OR_EMPTY);
        }
    }

    @Override
    public Void execute(DeleteAdventureLorebookEntry command) {

        domainService.deleteLorebookEntry(command);

        return null;
    }
}
