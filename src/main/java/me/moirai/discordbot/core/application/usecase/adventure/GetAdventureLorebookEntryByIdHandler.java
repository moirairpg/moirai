package me.moirai.discordbot.core.application.usecase.adventure;

import org.apache.commons.lang3.StringUtils;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.usecase.adventure.request.GetAdventureLorebookEntryById;
import me.moirai.discordbot.core.application.usecase.adventure.result.GetAdventureLorebookEntryResult;
import me.moirai.discordbot.core.domain.adventure.AdventureLorebookEntry;
import me.moirai.discordbot.core.domain.adventure.AdventureService;

@UseCaseHandler
public class GetAdventureLorebookEntryByIdHandler extends AbstractUseCaseHandler<GetAdventureLorebookEntryById, GetAdventureLorebookEntryResult> {

    private final AdventureService domainService;

    public GetAdventureLorebookEntryByIdHandler(AdventureService domainService) {
        this.domainService = domainService;
    }

    @Override
    public void validate(GetAdventureLorebookEntryById query) {

        if (StringUtils.isBlank(query.getEntryId())) {
            throw new IllegalArgumentException("Lorebook entry ID cannot be null");
        }

        if (StringUtils.isBlank(query.getAdventureId())) {
            throw new IllegalArgumentException("Adventure ID cannot be null");
        }
    }

    @Override
    public GetAdventureLorebookEntryResult execute(GetAdventureLorebookEntryById query) {

        AdventureLorebookEntry entry = domainService.findLorebookEntryById(query);

        return mapResult(entry);
    }

    private GetAdventureLorebookEntryResult mapResult(AdventureLorebookEntry entry) {

        return GetAdventureLorebookEntryResult.builder()
                .id(entry.getId())
                .name(entry.getName())
                .regex(entry.getRegex())
                .description(entry.getDescription())
                .playerDiscordId(entry.getPlayerDiscordId())
                .isPlayerCharacter(entry.isPlayerCharacter())
                .creationDate(entry.getCreationDate())
                .lastUpdateDate(entry.getLastUpdateDate())
                .build();
    }
}
