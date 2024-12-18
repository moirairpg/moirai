package me.moirai.discordbot.core.application.usecase.adventure;

import static io.micrometer.common.util.StringUtils.isBlank;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.usecase.adventure.request.UpdateAdventureLorebookEntry;
import me.moirai.discordbot.core.application.usecase.adventure.result.UpdateAdventureLorebookEntryResult;
import me.moirai.discordbot.core.domain.adventure.AdventureLorebookEntry;
import me.moirai.discordbot.core.domain.adventure.AdventureService;
import reactor.core.publisher.Mono;

@UseCaseHandler
public class UpdateAdventureLorebookEntryHandler
        extends AbstractUseCaseHandler<UpdateAdventureLorebookEntry, Mono<UpdateAdventureLorebookEntryResult>> {

    private final AdventureService service;

    public UpdateAdventureLorebookEntryHandler(AdventureService service) {
        this.service = service;
    }

    @Override
    public void validate(UpdateAdventureLorebookEntry command) {

        if (isBlank(command.getId())) {

            throw new IllegalArgumentException("Lorebook Entry ID cannot be null");
        }

        if (isBlank(command.getAdventureId())) {

            throw new IllegalArgumentException("Adventure ID cannot be null");
        }

        if (isBlank(command.getName())) {

            throw new IllegalArgumentException("Adventure name cannot be null");
        }

        if (isBlank(command.getDescription())) {

            throw new IllegalArgumentException("Adventure description cannot be null");
        }
    }

    @Override
    public Mono<UpdateAdventureLorebookEntryResult> execute(UpdateAdventureLorebookEntry command) {

        return service.updateLorebookEntry(command)
                .map(this::mapResult);
    }

    private UpdateAdventureLorebookEntryResult mapResult(AdventureLorebookEntry savedEntry) {

        return UpdateAdventureLorebookEntryResult.build(savedEntry.getLastUpdateDate());
    }
}
