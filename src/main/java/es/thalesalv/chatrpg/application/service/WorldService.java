package es.thalesalv.chatrpg.application.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.adapters.data.entity.WorldEntity;
import es.thalesalv.chatrpg.adapters.data.repository.WorldRepository;
import es.thalesalv.chatrpg.application.mapper.world.WorldDTOToEntity;
import es.thalesalv.chatrpg.application.mapper.world.WorldEntityToDTO;
import es.thalesalv.chatrpg.domain.enums.Visibility;
import es.thalesalv.chatrpg.domain.exception.InsufficientPermissionException;
import es.thalesalv.chatrpg.domain.exception.WorldNotFoundException;
import es.thalesalv.chatrpg.domain.model.chconf.World;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorldService {

    private final WorldDTOToEntity worldDTOToEntity;
    private final WorldEntityToDTO worldEntityToDTO;
    private final WorldRepository worldRepository;

    private static final String WORLD_ID_NOT_FOUND = "world with id WORLD_ID could not be found in database.";
    private static final Logger LOGGER = LoggerFactory.getLogger(WorldService.class);

    public List<World> retrieveAllWorlds(final String userId) {

        LOGGER.debug("Entering retrieveAllWorlds. userId -> {}", userId);
        return worldRepository.findAll()
                .stream()
                .filter(w -> hasReadPermissions(w, userId))
                .map(worldEntityToDTO)
                .toList();
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
        return worldEntityToDTO.apply(worldRepository.save(worldEntity));
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
        return worldEntityToDTO.apply(worldRepository.save(worldEntity));
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

    private boolean hasReadPermissions(final WorldEntity world, final String userId) {

        final boolean isPublic = Visibility.isPublic(world.getVisibility());
        final boolean isOwner = world.getOwner()
                .equals(userId);

        final boolean canRead = world.getReadPermissions()
                .contains(userId)
                || world.getWritePermissions()
                        .contains(userId);

        return isPublic || (isOwner || canRead);
    }

    private boolean hasWritePermissions(final WorldEntity world, final String userId) {

        final boolean isOwner = world.getOwner()
                .equals(userId);

        final boolean canWrite = world.getWritePermissions()
                .contains(userId);

        return isOwner || canWrite;
    }
}