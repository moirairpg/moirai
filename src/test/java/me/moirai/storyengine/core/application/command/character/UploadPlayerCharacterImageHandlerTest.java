package me.moirai.storyengine.core.application.command.character;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.storyengine.common.exception.NotFoundException;
import me.moirai.storyengine.core.domain.character.PlayerCharacterFixture;
import me.moirai.storyengine.core.port.inbound.character.UploadPlayerCharacterImage;
import me.moirai.storyengine.core.port.outbound.character.PlayerCharacterRepository;
import me.moirai.storyengine.core.port.outbound.storage.StoragePort;

@ExtendWith(MockitoExtension.class)
public class UploadPlayerCharacterImageHandlerTest {

    private static final String PREVIOUS_KEY = "characters/previous.png";
    private static final byte[] BYTES = new byte[] { 1, 2, 3 };
    private static final String CONTENT_TYPE = "image/png";

    @Mock
    private PlayerCharacterRepository repository;

    @Mock
    private StoragePort storagePort;

    @InjectMocks
    private UploadPlayerCharacterImageHandler handler;

    @Test
    void shouldDeleteThePreviousImageBeforeUploadingWhenTheCharacterAlreadyHasOne() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacterWithId();
        character.updateImageKey(PREVIOUS_KEY);

        var command = uploadFor(character.getPublicId());

        when(repository.findByPublicId(character.getPublicId())).thenReturn(Optional.of(character));

        // when
        handler.execute(command);

        // then
        var inOrder = inOrder(storagePort);
        inOrder.verify(storagePort).delete(PREVIOUS_KEY);
        inOrder.verify(storagePort).upload(any(), any(), any());
    }

    @Test
    void shouldNotDeleteAnythingWhenTheCharacterHasNoImage() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacterWithId();
        var command = uploadFor(character.getPublicId());

        when(repository.findByPublicId(character.getPublicId())).thenReturn(Optional.of(character));

        // when
        handler.execute(command);

        // then
        verify(storagePort, never()).delete(any());
        verify(storagePort).upload(any(), any(), any());
    }

    @Test
    void shouldUploadUnderTheGeneratedKeyAndSaveTheCharacterWhenTheImageIsUploaded() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacterWithId();
        var command = uploadFor(character.getPublicId());

        when(repository.findByPublicId(character.getPublicId())).thenReturn(Optional.of(character));

        // when
        handler.execute(command);

        // then
        var uploadedKey = ArgumentCaptor.forClass(String.class);
        verify(storagePort).upload(uploadedKey.capture(), any(), any());
        verify(repository).save(character);

        assertThat(uploadedKey.getValue()).isEqualTo(character.getImageKey());
        assertThat(uploadedKey.getValue()).startsWith("characters/" + character.getPublicId() + "/");
    }

    @Test
    void shouldReturnTheResolvedUrlOfTheNewKeyWhenTheImageIsUploaded() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacterWithId();
        var command = uploadFor(character.getPublicId());

        when(repository.findByPublicId(character.getPublicId())).thenReturn(Optional.of(character));
        when(storagePort.resolveUrl(any())).thenReturn("https://cdn/characters/new.png");

        // when
        var result = handler.execute(command);

        // then
        assertThat(result.imageUrl()).isEqualTo("https://cdn/characters/new.png");
    }

    @Test
    void shouldThrowExceptionWhenTheCharacterIsNotFound() {

        // given
        var characterId = UUID.randomUUID();
        var command = uploadFor(characterId);

        when(repository.findByPublicId(characterId)).thenReturn(Optional.empty());

        // then
        assertThrows(NotFoundException.class, () -> handler.execute(command));

        verify(storagePort, never()).upload(any(), any(), any());
        verify(repository, never()).save(any());
    }

    private UploadPlayerCharacterImage uploadFor(UUID characterId) {

        return new UploadPlayerCharacterImage(characterId, BYTES, CONTENT_TYPE, "png");
    }
}
