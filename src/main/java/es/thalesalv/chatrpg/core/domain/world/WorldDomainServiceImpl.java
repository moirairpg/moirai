package es.thalesalv.chatrpg.core.domain.world;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.common.exception.AssetAccessDeniedException;
import es.thalesalv.chatrpg.common.exception.AssetNotFoundException;
import es.thalesalv.chatrpg.common.exception.BusinessRuleViolationException;
import es.thalesalv.chatrpg.core.application.command.world.CreateWorld;
import es.thalesalv.chatrpg.core.application.command.world.CreateWorldLorebookEntry;
import es.thalesalv.chatrpg.core.application.command.world.DeleteWorld;
import es.thalesalv.chatrpg.core.application.command.world.DeleteWorldLorebookEntry;
import es.thalesalv.chatrpg.core.application.command.world.UpdateWorld;
import es.thalesalv.chatrpg.core.application.command.world.UpdateWorldLorebookEntry;
import es.thalesalv.chatrpg.core.application.query.world.GetWorldById;
import es.thalesalv.chatrpg.core.application.query.world.GetWorldLorebookEntryById;
import es.thalesalv.chatrpg.core.domain.Permissions;
import es.thalesalv.chatrpg.core.domain.Visibility;
import es.thalesalv.chatrpg.core.domain.port.TokenizerPort;
import io.micrometer.common.util.StringUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class WorldDomainServiceImpl implements WorldDomainService {

    private static final String LOREBOOK_ENTRY_TO_BE_UPDATED_WAS_NOT_FOUND = "Lorebook entry to be updated was not found";
    private static final String WORLD_TO_BE_VIEWED_WAS_NOT_FOUND = "World to be viewed was not found";
    private static final String USER_DOES_NOT_HAVE_PERMISSION_TO_MODIFY_THIS_WORLD = "User does not have permission to modify this world";

    @Value("${chatrpg.validation.token-limits.world.initial-prompt}")
    private int adventureStartTokenLimit;

    @Value("${chatrpg.validation.token-limits.world.lorebook-entry.description}")
    private int lorebookEntryDescriptionTokenLimit;

    @Value("${chatrpg.validation.token-limits.world.lorebook-entry.name}")
    private int lorebookEntryNameTokenLimit;

    private final WorldLorebookEntryRepository lorebookEntryRepository;
    private final WorldRepository repository;
    private final TokenizerPort tokenizerPort;

    @Override
    public World getWorldById(GetWorldById query) {

        World world = repository.findById(query.getId())
                .orElseThrow(() -> new AssetNotFoundException(WORLD_TO_BE_VIEWED_WAS_NOT_FOUND));

        if (!world.canUserRead(query.getRequesterDiscordId())) {
            throw new AssetAccessDeniedException("User does not have permission to view this world");
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
    public World createFrom(CreateWorld command) {

        Permissions permissions = Permissions.builder()
                .ownerDiscordId(command.getRequesterDiscordId())
                .usersAllowedToRead(command.getReaderUsers())
                .usersAllowedToWrite(command.getWriterUsers())
                .build();

        World world = World.builder()
                .name(command.getName())
                .description(command.getDescription())
                .adventureStart(command.getAdventureStart())
                .visibility(Visibility.fromString(command.getVisibility()))
                .permissions(permissions)
                .creatorDiscordId(command.getRequesterDiscordId())
                .build();

        validateTokenCount(world);

        return repository.save(world);
    }

    @Override
    public World update(UpdateWorld command) {

        World world = repository.findById(command.getId())
                .orElseThrow(() -> new AssetNotFoundException("World to be updated was not found"));

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

        if (command.getVisibility().equalsIgnoreCase(Visibility.PUBLIC.name())) {
            world.makePublic();
        } else if (command.getVisibility().equalsIgnoreCase(Visibility.PRIVATE.name())) {
            world.makePrivate();
        }

        CollectionUtils.emptyIfNull(command.getReaderUsersToAdd())
                .stream()
                .filter(discordUserId -> !world.canUserRead(discordUserId))
                .forEach(world::addReaderUser);

        CollectionUtils.emptyIfNull(command.getWriterUsersToAdd())
                .stream()
                .filter(discordUserId -> !world.canUserWrite(discordUserId))
                .forEach(world::addWriterUser);

        CollectionUtils.emptyIfNull(command.getReaderUsersToRemove())
                .forEach(world::removeReaderUser);

        CollectionUtils.emptyIfNull(command.getWriterUsersToRemove())
                .forEach(world::removeWriterUser);

        validateTokenCount(world);

        return repository.save(world);
    }

    @Override
    public WorldLorebookEntry findWorldLorebookEntryById(GetWorldLorebookEntryById query) {

        World world = repository.findById(query.getWorldId())
                .orElseThrow(() -> new AssetNotFoundException("World to be updated was not found"));

        if (!world.canUserRead(query.getRequesterDiscordId())) {
            throw new AssetAccessDeniedException(USER_DOES_NOT_HAVE_PERMISSION_TO_MODIFY_THIS_WORLD);
        }

        return lorebookEntryRepository.findById(query.getEntryId())
                .orElseThrow(() -> new AssetNotFoundException(LOREBOOK_ENTRY_TO_BE_UPDATED_WAS_NOT_FOUND));
    }

    @Override
    public WorldLorebookEntry createLorebookEntry(CreateWorldLorebookEntry command) {

        World world = repository.findById(command.getWorldId())
                .orElseThrow(() -> new AssetNotFoundException("World to be updated was not found"));

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

        validateTokenCount(lorebookEntry);

        return lorebookEntryRepository.save(lorebookEntry);
    }

    @Override
    public WorldLorebookEntry updateLorebookEntry(UpdateWorldLorebookEntry command) {

        World world = repository.findById(command.getWorldId())
                .orElseThrow(() -> new AssetNotFoundException("World to be updated was not found"));

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
            lorebookEntry.assignPlayer(command.getRequesterDiscordId());
        } else if (!command.isPlayerCharacter()) {
            lorebookEntry.unassignPlayer();
        }

        validateTokenCount(lorebookEntry);

        return lorebookEntryRepository.save(lorebookEntry);
    }

    @Override
    public void deleteLorebookEntry(DeleteWorldLorebookEntry command) {

        World world = repository.findById(command.getWorldId())
                .orElseThrow(() -> new AssetNotFoundException("World to be updated was not found"));

        if (!world.canUserWrite(command.getRequesterDiscordId())) {
            throw new AssetAccessDeniedException(USER_DOES_NOT_HAVE_PERMISSION_TO_MODIFY_THIS_WORLD);
        }

        lorebookEntryRepository.findById(command.getLorebookEntryId())
                .orElseThrow(() -> new AssetNotFoundException(LOREBOOK_ENTRY_TO_BE_UPDATED_WAS_NOT_FOUND));

        lorebookEntryRepository.deleteById(command.getLorebookEntryId());
    }

    private void validateTokenCount(WorldLorebookEntry lorebookEntry) {

        int lorebookEntryNameTokenCount = tokenizerPort.getTokenCountFrom(lorebookEntry.getName());
        if (lorebookEntryNameTokenCount > lorebookEntryNameTokenLimit) {
            throw new BusinessRuleViolationException("Amount of tokens in lorebook entry name surpasses allowed limit");
        }

        int lorebookEntryDescriptionTokenCount = tokenizerPort.getTokenCountFrom(lorebookEntry.getDescription());
        if (lorebookEntryDescriptionTokenCount > lorebookEntryDescriptionTokenLimit) {
            throw new BusinessRuleViolationException(
                    "Amount of tokens in lorebook entry description surpasses allowed limit");
        }
    }

    private void validateTokenCount(World world) {

        int adventureStartTokenCount = tokenizerPort.getTokenCountFrom(world.getAdventureStart());
        if (adventureStartTokenCount > adventureStartTokenLimit) {
            throw new BusinessRuleViolationException("Amount of tokens in initial prompt surpasses allowed limit");
        }
    }
}
