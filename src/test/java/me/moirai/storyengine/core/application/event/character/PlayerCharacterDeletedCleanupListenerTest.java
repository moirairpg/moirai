package me.moirai.storyengine.core.application.event.character;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.storyengine.core.domain.character.PlayerCharacter;
import me.moirai.storyengine.core.domain.character.PlayerCharacterDeletedEvent;
import me.moirai.storyengine.core.domain.character.PlayerCharacterFixture;
import me.moirai.storyengine.core.port.outbound.character.PlayerCharacterVectorSearchPort;
import me.moirai.storyengine.core.port.outbound.storage.StoragePort;

@ExtendWith(MockitoExtension.class)
public class PlayerCharacterDeletedCleanupListenerTest {

    @Mock
    private StoragePort storagePort;

    @Mock
    private PlayerCharacterVectorSearchPort vectorSearchPort;

    @InjectMocks
    private PlayerCharacterDeletedCleanupListener listener;

    @Test
    void shouldRemoveTheImageAndTheVectorWhenTheCharacterIsDeleted() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacterWithId();
        character.updateImageKey("characters/volin.png");

        // when
        listener.onPlayerCharacterDeleted(deletionEventFor(character));

        // then
        verify(storagePort).delete("characters/volin.png");
        verify(vectorSearchPort).delete(character.getPublicId());
    }

    @Test
    void shouldSkipTheImageWhenTheCharacterHasNone() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacterWithId();

        // when
        listener.onPlayerCharacterDeleted(deletionEventFor(character));

        // then
        verify(storagePort, never()).delete(any());
        verify(vectorSearchPort).delete(character.getPublicId());
    }

    @Test
    void shouldStillRemoveTheVectorWhenTheImageDeleteFails() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacterWithId();
        character.updateImageKey("characters/volin.png");

        doThrow(new RuntimeException("storage down")).when(storagePort).delete(any());

        // when
        listener.onPlayerCharacterDeleted(deletionEventFor(character));

        // then
        verify(vectorSearchPort).delete(character.getPublicId());
    }

    private PlayerCharacterDeletedEvent deletionEventFor(PlayerCharacter character) {

        character.communicateCharacterDeleted();

        return (PlayerCharacterDeletedEvent) character.drainEvents().getFirst();
    }
}
