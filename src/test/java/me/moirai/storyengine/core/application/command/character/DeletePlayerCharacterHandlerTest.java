package me.moirai.storyengine.core.application.command.character;

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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import me.moirai.storyengine.common.exception.NotFoundException;
import me.moirai.storyengine.core.domain.character.PlayerCharacterDeletedEvent;
import me.moirai.storyengine.core.domain.character.PlayerCharacterFixture;
import me.moirai.storyengine.core.port.inbound.character.DeletePlayerCharacter;
import me.moirai.storyengine.core.port.outbound.character.PlayerCharacterRepository;
import me.moirai.storyengine.core.port.outbound.character.PlayerCharacterVectorSearchPort;
import me.moirai.storyengine.core.port.outbound.storage.StoragePort;

@ExtendWith(MockitoExtension.class)
public class DeletePlayerCharacterHandlerTest {

    @Mock
    private PlayerCharacterRepository repository;

    @Mock
    private PlayerCharacterVectorSearchPort vectorSearchPort;

    @Mock
    private StoragePort storagePort;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private DeletePlayerCharacterHandler handler;

    @Test
    void shouldPublishDeletedEventBeforeDeletingTheAggregate() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacterWithId();
        var command = new DeletePlayerCharacter(character.getPublicId());

        when(repository.findByPublicId(character.getPublicId())).thenReturn(Optional.of(character));

        // when
        handler.execute(command);

        // then
        var inOrder = inOrder(eventPublisher, repository);
        inOrder.verify(eventPublisher).publishEvent(any(PlayerCharacterDeletedEvent.class));
        inOrder.verify(repository).deleteByPublicId(character.getPublicId());
    }

    @Test
    void shouldThrowExceptionWhenCharacterIsNotFound() {

        // given
        var characterId = UUID.randomUUID();
        var command = new DeletePlayerCharacter(characterId);

        when(repository.findByPublicId(characterId)).thenReturn(Optional.empty());

        // then
        assertThrows(NotFoundException.class, () -> handler.execute(command));

        verify(repository, never()).deleteByPublicId(any());
        verify(eventPublisher, never()).publishEvent(any());
        verify(storagePort, never()).delete(any());
        verify(vectorSearchPort, never()).delete(any());
    }
}
