package me.moirai.discordbot.core.application.usecase.world;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.usecase.world.request.CreateWorldLorebookEntry;
import me.moirai.discordbot.core.application.usecase.world.result.CreateWorldLorebookEntryResult;
import me.moirai.discordbot.core.domain.world.WorldLorebookEntry;
import me.moirai.discordbot.core.domain.world.WorldService;
import io.micrometer.common.util.StringUtils;

@UseCaseHandler
public class CreateWorldLorebookEntryHandler
        extends AbstractUseCaseHandler<CreateWorldLorebookEntry, CreateWorldLorebookEntryResult> {

    private final WorldService domainService;

    public CreateWorldLorebookEntryHandler(WorldService domainService) {
        this.domainService = domainService;
    }

    @Override
    public void validate(CreateWorldLorebookEntry command) {

        if (StringUtils.isBlank(command.getWorldId())) {

            throw new IllegalArgumentException("World ID cannot be null");
        }

        if (StringUtils.isBlank(command.getName())) {

            throw new IllegalArgumentException("Name cannot be null");
        }

        if (StringUtils.isBlank(command.getDescription())) {

            throw new IllegalArgumentException("Description cannot be null");
        }
    }

    @Override
    public CreateWorldLorebookEntryResult execute(CreateWorldLorebookEntry command) {

        WorldLorebookEntry entry = domainService.createLorebookEntry(command);
        return CreateWorldLorebookEntryResult.build(entry.getId());
    }
}
