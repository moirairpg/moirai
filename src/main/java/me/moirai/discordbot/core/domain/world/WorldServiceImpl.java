package me.moirai.discordbot.core.domain.world;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import io.micrometer.common.util.StringUtils;
import me.moirai.discordbot.common.annotation.DomainService;
import me.moirai.discordbot.common.exception.AssetAccessDeniedException;
import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.common.exception.ModerationException;
import me.moirai.discordbot.core.application.port.TextModerationPort;
import me.moirai.discordbot.core.application.usecase.world.request.CreateWorld;
import me.moirai.discordbot.core.application.usecase.world.request.CreateWorldLorebookEntry;
import me.moirai.discordbot.core.application.usecase.world.request.DeleteWorld;
import me.moirai.discordbot.core.application.usecase.world.request.DeleteWorldLorebookEntry;
import me.moirai.discordbot.core.application.usecase.world.request.GetWorldById;
import me.moirai.discordbot.core.application.usecase.world.request.GetWorldLorebookEntryById;
import me.moirai.discordbot.core.application.usecase.world.request.UpdateWorld;
import me.moirai.discordbot.core.application.usecase.world.request.UpdateWorldLorebookEntry;
import me.moirai.discordbot.core.domain.Permissions;
import me.moirai.discordbot.core.domain.Visibility;
import me.moirai.discordbot.core.domain.channelconfig.Moderation;
import reactor.core.publisher.Mono;

@DomainService
public class WorldServiceImpl implements WorldService {

    private static final String WORLD_FLAGGED_BY_MODERATION = "Persona flagged by moderation";
    private static final String WORLD_TO_BE_UPDATED_WAS_NOT_FOUND = "World to be updated was not found";
    private static final String WORLD_TO_BE_VIEWED_WAS_NOT_FOUND = "World to be viewed was not found";
    private static final String USER_DOES_NOT_HAVE_PERMISSION_TO_MODIFY_THIS_WORLD = "User does not have permission to modify this world";
    private static final String USER_DOES_NOT_HAVE_PERMISSION_TO_VIEW_THIS_WORLD = "User does not have permission to view this world";
    private static final String LOREBOOK_ENTRY_TO_BE_UPDATED_WAS_NOT_FOUND = "Lorebook entry to be updated was not found";
    private static final String LOREBOOK_ENTRY_TO_BE_DELETED_WAS_NOT_FOUND = "Lorebook entry to be deleted was not found";
    private static final String LOREBOOK_ENTRY_TO_BE_VIEWED_NOT_FOUND = "Lorebook entry to be viewed was not found";

    private final TextModerationPort moderationPort;
    private final WorldLorebookEntryRepository lorebookEntryRepository;
    private final WorldRepository repository;

    public WorldServiceImpl(TextModerationPort moderationPort,
            WorldLorebookEntryRepository lorebookEntryRepository,
            WorldRepository repository) {

        this.moderationPort = moderationPort;
        this.lorebookEntryRepository = lorebookEntryRepository;
        this.repository = repository;
    }

    @Override
    public World getWorldById(GetWorldById query) {

        World world = repository.findById(query.getId())
                .orElseThrow(() -> new AssetNotFoundException(WORLD_TO_BE_VIEWED_WAS_NOT_FOUND));

        if (!world.canUserRead(query.getRequesterDiscordId())) {
            throw new AssetAccessDeniedException(USER_DOES_NOT_HAVE_PERMISSION_TO_VIEW_THIS_WORLD);
        }

        return world;
    }

    @Override
    public void deleteWorld(DeleteWorld command) {

        World world = repository.findById(command.getId())
                .orElseThrow(() -> new AssetNotFoundException("World to be deleted was not found"));

        if (!world.canUserWrite(command.getRequesterDiscordId())) {
            throw new AssetAccessDeniedException(USER_DOES_NOT_HAVE_PERMISSION_TO_MODIFY_THIS_WORLD);
        }

        repository.deleteById(command.getId());
    }

    @Override
    public Mono<World> createFrom(CreateWorld command) {

        return moderateContent(command.getAdventureStart())
                .flatMap(__ -> moderateContent(command.getName()))
                .flatMap(__ -> moderateContent(command.getDescription()))
                .map(__ -> {
                    Permissions permissions = Permissions.builder()
                            .ownerDiscordId(command.getRequesterDiscordId())
                            .usersAllowedToRead(command.getUsersAllowedToRead())
                            .usersAllowedToWrite(command.getUsersAllowedToWrite())
                            .build();

                    World world = World.builder()
                            .name(command.getName())
                            .description(command.getDescription())
                            .adventureStart(command.getAdventureStart())
                            .visibility(Visibility.fromString(command.getVisibility()))
                            .permissions(permissions)
                            .creatorDiscordId(command.getRequesterDiscordId())
                            .build();

                    return repository.save(world);
                });

    }

    @Override
    public Mono<World> update(UpdateWorld command) {

        return moderateContent(command.getAdventureStart())
                .flatMap(__ -> moderateContent(command.getName()))
                .flatMap(__ -> moderateContent(command.getDescription()))
                .map(__ -> {
                    World world = repository.findById(command.getId())
                            .orElseThrow(() -> new AssetNotFoundException(WORLD_TO_BE_UPDATED_WAS_NOT_FOUND));

                    if (!world.canUserWrite(command.getRequesterDiscordId())) {
                        throw new AssetAccessDeniedException(USER_DOES_NOT_HAVE_PERMISSION_TO_MODIFY_THIS_WORLD);
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
                });
    }

    @Override
    public WorldLorebookEntry findLorebookEntryByPlayerDiscordId(String playerDiscordId, String worldId) {

        repository.findById(worldId)
                .orElseThrow(() -> new AssetNotFoundException(WORLD_TO_BE_VIEWED_WAS_NOT_FOUND));

                return lorebookEntryRepository.findByPlayerDiscordId(playerDiscordId, worldId)
                        .orElseThrow(() -> new AssetNotFoundException(LOREBOOK_ENTRY_TO_BE_VIEWED_NOT_FOUND));
    }

    @Override
    public List<WorldLorebookEntry> findAllLorebookEntriesByRegex(String valueToMatch, String worldId) {

        repository.findById(worldId)
                .orElseThrow(() -> new AssetNotFoundException(WORLD_TO_BE_VIEWED_WAS_NOT_FOUND));

        return lorebookEntryRepository.findAllByRegex(valueToMatch, worldId);
    }

    @Override
    public WorldLorebookEntry findLorebookEntryById(GetWorldLorebookEntryById query) {

        World world = repository.findById(query.getWorldId())
                .orElseThrow(() -> new AssetNotFoundException(WORLD_TO_BE_VIEWED_WAS_NOT_FOUND));

        if (!world.canUserRead(query.getRequesterDiscordId())) {
            throw new AssetAccessDeniedException(USER_DOES_NOT_HAVE_PERMISSION_TO_VIEW_THIS_WORLD);
        }

        return lorebookEntryRepository.findById(query.getEntryId())
                .orElseThrow(() -> new AssetNotFoundException(LOREBOOK_ENTRY_TO_BE_VIEWED_NOT_FOUND));
    }

    @Override
    public WorldLorebookEntry createLorebookEntry(CreateWorldLorebookEntry command) {

        World world = repository.findById(command.getWorldId())
                .orElseThrow(() -> new AssetNotFoundException(WORLD_TO_BE_UPDATED_WAS_NOT_FOUND));

        if (!world.canUserWrite(command.getRequesterDiscordId())) {
            throw new AssetAccessDeniedException(USER_DOES_NOT_HAVE_PERMISSION_TO_MODIFY_THIS_WORLD);
        }

        WorldLorebookEntry lorebookEntry = WorldLorebookEntry.builder()
                .name(command.getName())
                .regex(command.getRegex())
                .description(command.getDescription())
                .playerDiscordId(command.getPlayerDiscordId())
                .isPlayerCharacter(command.isPlayerCharacter())
                .worldId(command.getWorldId())
                .creatorDiscordId(command.getRequesterDiscordId())
                .build();

        return lorebookEntryRepository.save(lorebookEntry);
    }

    @Override
    public WorldLorebookEntry updateLorebookEntry(UpdateWorldLorebookEntry command) {

        World world = repository.findById(command.getWorldId())
                .orElseThrow(() -> new AssetNotFoundException(WORLD_TO_BE_UPDATED_WAS_NOT_FOUND));

        if (!world.canUserWrite(command.getRequesterDiscordId())) {
            throw new AssetAccessDeniedException(USER_DOES_NOT_HAVE_PERMISSION_TO_MODIFY_THIS_WORLD);
        }

        WorldLorebookEntry lorebookEntry = lorebookEntryRepository.findById(command.getId())
                .orElseThrow(() -> new AssetNotFoundException(LOREBOOK_ENTRY_TO_BE_UPDATED_WAS_NOT_FOUND));

        if (StringUtils.isNotBlank(command.getName())) {
            lorebookEntry.updateName(command.getName());
        }

        if (StringUtils.isNotBlank(command.getRegex())) {
            lorebookEntry.updateRegex(command.getRegex());
        }

        if (StringUtils.isNotBlank(command.getDescription())) {
            lorebookEntry.updateDescription(command.getDescription());
        }

        if (command.isPlayerCharacter()) {
            lorebookEntry.assignPlayer(command.getPlayerDiscordId());
        } else {
            lorebookEntry.unassignPlayer();
        }

        return lorebookEntryRepository.save(lorebookEntry);
    }

    @Override
    public void deleteLorebookEntry(DeleteWorldLorebookEntry command) {

        World world = repository.findById(command.getWorldId())
                .orElseThrow(() -> new AssetNotFoundException(WORLD_TO_BE_UPDATED_WAS_NOT_FOUND));

        if (!world.canUserWrite(command.getRequesterDiscordId())) {
            throw new AssetAccessDeniedException(USER_DOES_NOT_HAVE_PERMISSION_TO_MODIFY_THIS_WORLD);
        }

        lorebookEntryRepository.findById(command.getLorebookEntryId())
                .orElseThrow(() -> new AssetNotFoundException(LOREBOOK_ENTRY_TO_BE_DELETED_WAS_NOT_FOUND));

        lorebookEntryRepository.deleteById(command.getLorebookEntryId());
    }

    private Mono<List<String>> moderateContent(String personality) {

        if (StringUtils.isBlank(personality)) {
            return Mono.just(Collections.emptyList());
        }

        return getTopicsFlaggedByModeration(personality)
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
