package me.moirai.storyengine.core.application.event.character;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import me.moirai.storyengine.core.domain.character.PlayerCharacterDeletedEvent;
import me.moirai.storyengine.core.port.outbound.character.PlayerCharacterVectorSearchPort;
import me.moirai.storyengine.core.port.outbound.storage.StoragePort;

@Component
public class PlayerCharacterDeletedCleanupListener {

    private static final Logger LOG = LoggerFactory.getLogger(PlayerCharacterDeletedCleanupListener.class);

    private static final String IMAGE_CLEANUP_FAILED = "Orphaned image after deleting character {}: key {}";
    private static final String VECTOR_CLEANUP_FAILED = "Orphaned vector after deleting character {}";

    private final StoragePort storagePort;
    private final PlayerCharacterVectorSearchPort vectorSearchPort;

    public PlayerCharacterDeletedCleanupListener(
            StoragePort storagePort,
            PlayerCharacterVectorSearchPort vectorSearchPort) {

        this.storagePort = storagePort;
        this.vectorSearchPort = vectorSearchPort;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPlayerCharacterDeleted(PlayerCharacterDeletedEvent event) {

        removeImage(event);
        removeVector(event);
    }

    private void removeImage(PlayerCharacterDeletedEvent event) {

        if (event.getImageKey() == null) {
            return;
        }

        try {
            storagePort.delete(event.getImageKey());
        } catch (RuntimeException e) {
            LOG.error(IMAGE_CLEANUP_FAILED, event.getPublicId(), event.getImageKey(), e);
        }
    }

    private void removeVector(PlayerCharacterDeletedEvent event) {

        try {
            vectorSearchPort.delete(event.getPublicId());
        } catch (RuntimeException e) {
            LOG.error(VECTOR_CLEANUP_FAILED, event.getPublicId(), e);
        }
    }
}
