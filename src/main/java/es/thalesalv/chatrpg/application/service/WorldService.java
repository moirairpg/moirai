package es.thalesalv.chatrpg.application.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntryEntity;
import es.thalesalv.chatrpg.adapters.data.entity.WorldEntity;
import es.thalesalv.chatrpg.adapters.data.repository.LorebookEntryRepository;
import es.thalesalv.chatrpg.adapters.data.repository.WorldRepository;
import es.thalesalv.chatrpg.application.mapper.lorebook.LorebookEntryDTOToEntity;
import es.thalesalv.chatrpg.application.mapper.lorebook.LorebookEntryEntityToDTO;
import es.thalesalv.chatrpg.application.mapper.world.WorldDTOToEntity;
import es.thalesalv.chatrpg.application.mapper.world.WorldEntityToDTO;
import es.thalesalv.chatrpg.domain.criteria.AssetSpecification;
import es.thalesalv.chatrpg.domain.enums.Visibility;
import es.thalesalv.chatrpg.domain.exception.InsufficientPermissionException;
import es.thalesalv.chatrpg.domain.exception.LorebookEntryNotFoundException;
import es.thalesalv.chatrpg.domain.exception.WorldNotFoundException;
import es.thalesalv.chatrpg.domain.model.api.PagedResponse;
import es.thalesalv.chatrpg.domain.model.bot.LorebookEntry;
import es.thalesalv.chatrpg.domain.model.bot.World;
import es.thalesalv.chatrpg.domain.model.discord.DiscordUserData;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorldService {

    private final WorldDTOToEntity worldDTOToEntity;
    private final WorldEntityToDTO worldEntityToDTO;
    private final LorebookEntryDTOToEntity lorebookEntryDTOToEntity;
    private final LorebookEntryEntityToDTO lorebookEntryEntityToDTO;

    private final WorldRepository worldRepository;
    private final LorebookEntryRepository lorebookEntryRepository;

    private final DiscordAuthService discordAuthService;

    private static final String LOREBOOK_ID_NOT_FOUND = "lorebook with id LOREBOOK_ID could not be found in database.";
    private static final String LOREBOOK_ENTRY_ID_NOT_FOUND = "lorebook entry with id LOREBOOK_ENTRY_ID could not be found in database.";
    private static final String WORLD_ID_NOT_FOUND = "world with id WORLD_ID could not be found in database.";
    private static final Logger LOGGER = LoggerFactory.getLogger(WorldService.class);

    public List<World> retrieveAllWorlds(final String userId) {

        LOGGER.debug("Entering retrieveAllWorlds. userId -> {}", userId);
        final List<World> worlds = worldRepository.findAll()
                .stream()
                .filter(w -> hasReadPermissions(w, userId))
                .map(worldEntityToDTO)
                .toList();

        final Map<String, String> discordUsers = retrieveOwnerUsername(worlds);
        return addOwnerToWorlds(worlds, discordUsers);
    }

    public World retrieveWorldById(final String worldId, final String userId) {

        LOGGER.debug("Entering retrieveWorldById. worldId -> {}, userId -> {}", worldId, userId);
        return worldRepository.findById(worldId)
                .filter(w -> hasReadPermissions(w, userId))
                .map(worldEntityToDTO)
                .orElseThrow(() -> new WorldNotFoundException(
                        "Error retrieving world by id: " + WORLD_ID_NOT_FOUND.replace("WORLD_ID", worldId)));
    }

    public World saveWorld(final World world) {

        LOGGER.debug("Entering saveWorld. world -> {}", world);
        final WorldEntity worldEntity = worldDTOToEntity.apply(world);
        final List<LorebookEntryEntity> lorebook = worldEntity.getLorebook();

        worldEntity.setLorebook(null);
        final WorldEntity savedWorld = worldRepository.save(worldEntity);
        savedWorld.setLorebook(Optional.ofNullable(lorebook)
                .map(lb -> lb.stream()
                        .map(entry -> {
                            entry.setWorld(savedWorld);
                            return lorebookEntryRepository.save(entry);
                        })
                        .collect(Collectors.toList()))
                .orElse(new ArrayList<>()));

        return worldEntityToDTO.apply(savedWorld);
    }

    public World updateWorld(final String worldId, final World world, final String userId) {

        LOGGER.debug("Entering updateWorld. worldId -> {}, userId -> {}, world -> {}", worldId, userId, world);
        worldRepository.findById(worldId)
                .orElseThrow(() -> new WorldNotFoundException(
                        "Error updating world: " + WORLD_ID_NOT_FOUND.replace("WORLD_ID", worldId)));

        final WorldEntity worldEntity = worldDTOToEntity.apply(world);
        if (!hasWritePermissions(worldEntity, userId)) {
            throw new InsufficientPermissionException("Not enough permissions to modify this world");
        }

        worldEntity.setId(worldId);
        final List<LorebookEntryEntity> lorebook = worldEntity.getLorebook();

        worldEntity.setLorebook(null);
        final WorldEntity savedWorld = worldRepository.save(worldEntity);
        savedWorld.setLorebook(Optional.ofNullable(lorebook)
                .map(lb -> lb.stream()
                        .map(entry -> {
                            entry.setWorld(savedWorld);
                            return lorebookEntryRepository.save(entry);
                        })
                        .collect(Collectors.toList()))
                .orElse(new ArrayList<>()));

        return worldEntityToDTO.apply(worldRepository.save(savedWorld));
    }

    public void deleteWorld(final String worldId, final String userId) {

        LOGGER.debug("Entering deleteWorld. worldId -> {}, userId -> {}", worldId, userId);
        worldRepository.findById(worldId)
                .ifPresentOrElse(world -> {
                    if (!hasWritePermissions(world, userId)) {
                        throw new InsufficientPermissionException("Not enough permissions to delete this world");
                    }

                    worldRepository.delete(world);
                }, () -> {
                    throw new WorldNotFoundException(
                            "Error deleting world: " + WORLD_ID_NOT_FOUND.replace("WORLD_ID", worldId));
                });
    }

    public PagedResponse<World> retrieveAllWithPagination(final String requesterDiscordId, final String searchCriteria,
            final String searchField, final int pageNumber, final int amountOfItems, final String sortBy) {

        Page<WorldEntity> page;
        final String sortByField = StringUtils.isBlank(sortBy) ? "name" : sortBy;
        if (StringUtils.isBlank(searchField) || StringUtils.isBlank(searchCriteria)) {
            page = worldRepository.findAll(PageRequest.of(pageNumber - 1, amountOfItems, Sort.by(sortByField)));
            return buildWorldPage(requesterDiscordId, page);
        }

        final AssetSpecification<WorldEntity> spec = new AssetSpecification<>(searchField, searchCriteria);
        page = worldRepository.findAll(spec, PageRequest.of(pageNumber - 1, amountOfItems, Sort.by(sortByField)));

        return buildWorldPage(requesterDiscordId, page);
    }

    public LorebookEntry retrieveLorebookEntryById(final String lorebookEntryId, final String userId) {

        LOGGER.debug("Entering retrieveLorebookEntryById. lorebookEntryId -> {}. userId -> {}", lorebookEntryId,
                userId);

        final LorebookEntryEntity entryEntity = lorebookEntryRepository.findById(lorebookEntryId)
                .orElseThrow(() -> new LorebookEntryNotFoundException(
                        "Error retrieving lorebook entry: lorebook entry with id " + lorebookEntryId
                                + " could not be found in database."));

        if (!hasReadPermissions(entryEntity.getWorld(), userId)) {
            throw new InsufficientPermissionException("Not enough permissions to retrieve entries in this lorebook");
        }

        return lorebookEntryEntityToDTO.apply(entryEntity);
    }

    public List<LorebookEntry> retrieveAllLorebookEntriesInLorebook(final String worldId, final String userId) {

        LOGGER.debug("Entering retrieveAllLorebookEntriesInLorebook. worldId -> {}, userId -> {}", worldId, userId);
        return worldRepository.findById(worldId)
                .map(w -> {
                    if (!hasReadPermissions(w, userId)) {
                        throw new InsufficientPermissionException(
                                "Not enough permissions to retrieve entries in this lorebook");
                    }

                    return w.getLorebook();
                })
                .map(es -> es.stream()
                        .map(lorebookEntryEntityToDTO::apply)
                        .toList())
                .orElseThrow(() -> new WorldNotFoundException("The lorebook requested could not be found"));
    }

    public LorebookEntry saveLorebookEntry(final LorebookEntry lorebookEntry, final String worldId,
            final String userId) {

        LOGGER.debug("Entering saveLorebookEntry. worldId -> {}, userId -> {}, lorebookEntry -> {}", worldId, userId,
                lorebookEntry);

        return worldRepository.findById(worldId)
                .map(world -> {
                    if (!hasWritePermissions(world, userId)) {
                        throw new InsufficientPermissionException(
                                "Not enough permissions to add entries to this lorebook");
                    }

                    final LorebookEntryEntity entryEntity = lorebookEntryDTOToEntity.apply(lorebookEntry);
                    entryEntity.setWorld(world);
                    return lorebookEntryRepository.save(entryEntity);
                })
                .map(lorebookEntryEntityToDTO)
                .orElseThrow(() -> new WorldNotFoundException("Error saving lorebook entry to lorebook: "
                        + LOREBOOK_ID_NOT_FOUND.replace("LOREBOOK_ID", worldId)));
    }

    public LorebookEntry updateLorebookEntry(final String lorebookEntryId, final LorebookEntry lorebookEntry,
            final String userId) {

        LOGGER.debug("Entering updateLorebookEntry. lorebookEntryId -> {}, userId -> {}, lorebookEntry -> {}",
                lorebookEntryId, userId, lorebookEntry);

        final LorebookEntryEntity lorebookEntryEntity = lorebookEntryRepository.findById(lorebookEntryId)
                .orElseThrow(() -> new LorebookEntryNotFoundException("Error updating lorebook entry: "
                        + LOREBOOK_ENTRY_ID_NOT_FOUND.replace("LOREBOOK_ENTRY_ID", lorebookEntryId)));

        if (!hasWritePermissions(lorebookEntryEntity.getWorld(), userId)) {
            throw new InsufficientPermissionException("Not enough permissions to modify entries in this lorebook");
        }

        lorebookEntryEntity.setName(lorebookEntry.getName());
        lorebookEntryEntity.setDescription(lorebookEntry.getDescription());
        lorebookEntryEntity.setPlayerDiscordId(lorebookEntry.getPlayerDiscordId());
        lorebookEntryEntity.setRegex(lorebookEntry.getRegex());

        lorebookEntryRepository.save(lorebookEntryEntity);
        return lorebookEntryEntityToDTO.apply(lorebookEntryEntity);
    }

    public void deleteLorebookEntry(final String lorebookEntryId, final String userId) {

        LOGGER.debug("Entering deleteLorebookEntry. lorebookEntryId -> {}, userId -> {}", lorebookEntryId, userId);
        final LorebookEntryEntity entry = lorebookEntryRepository.findById(lorebookEntryId)
                .orElseThrow(() -> new LorebookEntryNotFoundException("Error deleting entry: lorebook entry with id "
                        + lorebookEntryId + " could not be found in database."));

        if (!hasWritePermissions(entry.getWorld(), userId)) {
            throw new InsufficientPermissionException("Not enough permissions to delete entries in this lorebook");
        }

        lorebookEntryRepository.delete(entry);
    }

    private boolean hasReadPermissions(final WorldEntity world, final String userId) {

        final List<String> readPermissions = Optional.ofNullable(world.getReadPermissions())
                .orElse(Collections.emptyList());

        final List<String> writePermissions = Optional.ofNullable(world.getWritePermissions())
                .orElse(Collections.emptyList());

        final boolean isPublic = Visibility.isPublic(world.getVisibility());
        final boolean isOwner = world.getOwnerDiscordId()
                .equals(userId);

        final boolean canRead = readPermissions.contains(userId) || writePermissions.contains(userId);

        return isPublic || (isOwner || canRead);
    }

    private boolean hasWritePermissions(final WorldEntity world, final String userId) {

        final List<String> writePermissions = Optional.ofNullable(world.getWritePermissions())
                .orElse(Collections.emptyList());

        final boolean isOwner = world.getOwnerDiscordId()
                .equals(userId);

        final boolean canWrite = writePermissions.contains(userId);

        return isOwner || canWrite;
    }

    private PagedResponse<World> buildWorldPage(final String requesterDiscordId, Page<WorldEntity> page) {

        final List<World> worlds = page.getContent()
                .stream()
                .filter(c -> hasReadPermissions(c, requesterDiscordId))
                .map(worldEntityToDTO)
                .collect(Collectors.toList());

        final Map<String, String> discordUsers = retrieveOwnerUsername(worlds);
        return PagedResponse.<World>builder()
                .currentPage(page.getNumber() + 1)
                .numberOfPages(page.getTotalPages())
                .data(addOwnerToWorlds(worlds, discordUsers))
                .totalNumberOfItems((int) page.getTotalElements())
                .numberOfItemsInPage(page.getNumberOfElements())
                .build();
    }

    private Map<String, String> retrieveOwnerUsername(List<World> worlds) {

        return worlds.stream()
                .map(world -> {
                    return world.getOwnerDiscordId();
                })
                .collect(Collectors.toSet())
                .stream()
                .map(discordUserId -> {
                    return discordAuthService.retrieveDiscordUserById(discordUserId);
                })
                .collect(Collectors.toMap(DiscordUserData::getId, DiscordUserData::getUsername, (w1, w2) -> w1));
    }

    private List<World> addOwnerToWorlds(List<World> worlds, Map<String, String> discordUsers) {

        return worlds.stream()
                .map(world -> {
                    discordUsers.entrySet()
                            .stream()
                            .filter(entry -> entry.getKey()
                                    .equals(world.getOwnerDiscordId()))
                            .forEach(entry -> {
                                world.setOwnerUsername(entry.getValue());
                            });

                    return world;
                })
                .collect(Collectors.toList());
    }
}