package me.moirai.storyengine.core.application.event.world;

import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import me.moirai.storyengine.core.domain.userdetails.UserDeletedEvent;
import me.moirai.storyengine.core.domain.world.World;
import me.moirai.storyengine.core.domain.world.WorldDeletedEvent;
import me.moirai.storyengine.core.port.outbound.storage.StoragePort;
import me.moirai.storyengine.core.port.outbound.world.WorldRepository;

@Component
public class WorldDomainEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(WorldDomainEventListener.class);

    private static final String IMAGE_CLEANUP_FAILED = "Orphaned image after deleting world {}: key {}";

    private final WorldRepository worldRepository;
    private final StoragePort storagePort;
    private final ApplicationEventPublisher eventPublisher;

    public WorldDomainEventListener(
            WorldRepository worldRepository,
            StoragePort storagePort,
            ApplicationEventPublisher eventPublisher) {

        this.worldRepository = worldRepository;
        this.storagePort = storagePort;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @EventListener
    public void onUserDeleted(UserDeletedEvent event) {

        var deletedIds = deleteOwnedWorlds(event.getUserId());

        worldRepository.findAllInvolving(event.getUserId()).stream()
                .filter(world -> !deletedIds.contains(world.getId()))
                .forEach(world -> revokeFrom(event.getUserId(), world));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onWorldDeleted(WorldDeletedEvent event) {

        if (event.getImageKey() == null) {
            return;
        }

        try {
            storagePort.delete(event.getImageKey());
        } catch (RuntimeException e) {
            LOG.error(IMAGE_CLEANUP_FAILED, event.getPublicId(), event.getImageKey(), e);
        }
    }

    private Set<Long> deleteOwnedWorlds(Long userId) {

        var owned = worldRepository.findAllOwnedBy(userId);

        owned.forEach(world -> {
            world.communicateWorldDeleted();
            world.drainEvents().forEach(eventPublisher::publishEvent);

            worldRepository.deleteByPublicId(world.getPublicId());
        });

        return owned.stream()
                .map(World::getId)
                .collect(Collectors.toSet());
    }

    private void revokeFrom(Long userId, World world) {

        world.revoke(userId);

        worldRepository.save(world);
    }
}
