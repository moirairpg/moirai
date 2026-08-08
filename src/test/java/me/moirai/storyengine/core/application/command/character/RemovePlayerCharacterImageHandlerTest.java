package me.moirai.storyengine.core.application.command.character;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.storyengine.common.exception.NotFoundException;
import me.moirai.storyengine.core.domain.character.PlayerCharacterFixture;
import me.moirai.storyengine.core.port.inbound.character.RemovePlayerCharacterImage;
import me.moirai.storyengine.core.port.outbound.character.PlayerCharacterRepository;
import me.moirai.storyengine.core.port.outbound.storage.StoragePort;

@ExtendWith(MockitoExtension.class)
public class RemovePlayerCharacterImageHandlerTest {

    private static final String IMAGE_KEY = "characters/volin.png";

    @Mock
    private PlayerCharacterRepository repository;

    @Mock
    private StoragePort storagePort;

    @InjectMocks
    private RemovePlayerCharacterImageHandler handler;

    @Test
    void shouldThrowExceptionWhenTheCharacterIdIsNull() {

        // given
        var command = new RemovePlayerCharacterImage(null);

        // then
        assertThrows(IllegalArgumentException.class, () -> handler.handle(command));
    }

    @Test
    void shouldDeleteTheImageAndClearTheKeyWhenTheCharacterHasOne() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacterWithId();
        character.updateImageKey(IMAGE_KEY);

        var command = new RemovePlayerCharacterImage(character.getPublicId());

        when(repository.findByPublicId(character.getPublicId())).thenReturn(Optional.of(character));

        // when
        handler.execute(command);

        // then
        verify(storagePort).delete(IMAGE_KEY);
        verify(repository).save(character);

        assertThat(character.getImageKey()).isNull();
    }

    @Test
    void shouldDoNothingWhenTheCharacterHasNoImage() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacterWithId();
        var command = new RemovePlayerCharacterImage(character.getPublicId());

        when(repository.findByPublicId(character.getPublicId())).thenReturn(Optional.of(character));

        // when
        handler.execute(command);

        // then
        verify(storagePort, never()).delete(any());
        verify(repository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenTheCharacterIsNotFound() {

        // given
        var characterId = UUID.randomUUID();
        var command = new RemovePlayerCharacterImage(characterId);

        when(repository.findByPublicId(characterId)).thenReturn(Optional.empty());

        // then
        assertThrows(NotFoundException.class, () -> handler.execute(command));

        verify(storagePort, never()).delete(any());
        verify(repository, never()).save(any());
    }
}
