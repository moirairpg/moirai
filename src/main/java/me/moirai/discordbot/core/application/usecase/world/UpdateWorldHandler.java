package me.moirai.discordbot.core.application.usecase.world;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.exception.AssetAccessDeniedException;
import me.moirai.discordbot.common.exception.ModerationException;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.port.TextModerationPort;
import me.moirai.discordbot.core.application.usecase.world.request.UpdateWorld;
import me.moirai.discordbot.core.application.usecase.world.result.UpdateWorldResult;
import me.moirai.discordbot.core.domain.Visibility;
import me.moirai.discordbot.core.domain.channelconfig.Moderation;
import me.moirai.discordbot.core.domain.world.World;
import me.moirai.discordbot.core.domain.world.WorldDomainRepository;
import me.moirai.discordbot.core.domain.world.WorldService;
import reactor.core.publisher.Mono;

@UseCaseHandler
public class UpdateWorldHandler extends AbstractUseCaseHandler<UpdateWorld, Mono<UpdateWorldResult>> {

    private static final String WORLD_FLAGGED_BY_MODERATION = "World flagged by moderation";
    private static final String ID_CANNOT_BE_NULL_OR_EMPTY = "World ID cannot be null or empty";
    private static final String PERMISSION_MODIFY_DENIED = "User does not have permission to modify this world";

    private final WorldDomainRepository repository;
    private final WorldService domainService;
    private final TextModerationPort moderationPort;

    public UpdateWorldHandler(WorldDomainRepository repository,
            WorldService domainService,
            TextModerationPort moderationPort) {

        this.repository = repository;
        this.domainService = domainService;
        this.moderationPort = moderationPort;
    }

    @Override
    public void validate(UpdateWorld command) {

        if (StringUtils.isBlank(command.getId())) {
            throw new IllegalArgumentException(ID_CANNOT_BE_NULL_OR_EMPTY);
        }
    }

    @Override
    public Mono<UpdateWorldResult> execute(UpdateWorld command) {

        return moderateContent(command.getAdventureStart())
                .flatMap(__ -> moderateContent(command.getName()))
                .map(__ -> {
                    World world = domainService.getWorldById(command.getId());
                    if (!world.canUserWrite(command.getRequesterDiscordId())) {
                        throw new AssetAccessDeniedException(PERMISSION_MODIFY_DENIED);
                    }

                    return world;
                })
                .map(world -> updateWorld(command, world))
                .map(WorldUpdated -> UpdateWorldResult.build(WorldUpdated.getLastUpdateDate()));
    }

    public World updateWorld(UpdateWorld command, World world) {

        if (!world.canUserWrite(command.getRequesterDiscordId())) {
            throw new AssetAccessDeniedException(PERMISSION_MODIFY_DENIED);
        }

        if (StringUtils.isNotBlank(command.getName())) {
            world.updateName(command.getName());
        }

        if (StringUtils.isNotBlank(command.getDescription())) {
            world.updateDescription(command.getDescription());
        }

        if (StringUtils.isNotBlank(command.getAdventureStart())) {
            world.updateAdventureStart(command.getAdventureStart());
        }

        if (StringUtils.isNotBlank(command.getVisibility())) {
            if (command.getVisibility().equalsIgnoreCase(Visibility.PUBLIC.name())) {
                world.makePublic();
            } else if (command.getVisibility().equalsIgnoreCase(Visibility.PRIVATE.name())) {
                world.makePrivate();
            }
        }

        CollectionUtils.emptyIfNull(command.getUsersAllowedToReadToAdd())
                .stream()
                .filter(userId -> !world.canUserRead(userId))
                .forEach(world::addReaderUser);

        CollectionUtils.emptyIfNull(command.getUsersAllowedToWriteToAdd())
                .stream()
                .filter(userId -> !world.canUserWrite(userId))
                .forEach(world::addWriterUser);

        CollectionUtils.emptyIfNull(command.getUsersAllowedToReadToRemove())
                .forEach(world::removeReaderUser);

        CollectionUtils.emptyIfNull(command.getUsersAllowedToWriteToRemove())
                .forEach(world::removeWriterUser);

        return repository.save(world);
    }

    private Mono<List<String>> moderateContent(String content) {

        if (StringUtils.isBlank(content)) {
            return Mono.just(Collections.emptyList());
        }

        return getTopicsFlaggedByModeration(content)
                .map(flaggedTopics -> {
                    if (CollectionUtils.isNotEmpty(flaggedTopics)) {
                        throw new ModerationException(WORLD_FLAGGED_BY_MODERATION, flaggedTopics);
                    }

                    return flaggedTopics;
                });
    }

    private Mono<List<String>> getTopicsFlaggedByModeration(String input) {

        return moderationPort.moderate(input)
                .map(result -> result.getModerationScores()
                        .entrySet()
                        .stream()
                        .filter(this::isTopicFlagged)
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList()));
    }

    private boolean isTopicFlagged(Entry<String, Double> entry) {
        return entry.getValue() > Moderation.PERMISSIVE.getThresholds().get(entry.getKey());
    }
}
