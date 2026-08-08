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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.storyengine.common.enums.CharacterClass;
import me.moirai.storyengine.common.exception.BusinessRuleViolationException;
import me.moirai.storyengine.common.exception.NotFoundException;
import me.moirai.storyengine.core.domain.character.PlayerCharacterFixture;
import me.moirai.storyengine.core.domain.userdetails.UserFixture;
import me.moirai.storyengine.core.port.inbound.character.UpdatePlayerCharacter;
import me.moirai.storyengine.core.port.outbound.character.PlayerCharacterRepository;
import me.moirai.storyengine.core.port.outbound.character.PlayerCharacterVectorSearchPort;
import me.moirai.storyengine.core.port.outbound.generation.EmbeddingPort;
import me.moirai.storyengine.core.port.outbound.storage.StoragePort;
import me.moirai.storyengine.core.port.outbound.userdetails.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UpdatePlayerCharacterHandlerTest {

    private static final float[] VECTOR = new float[] { 0.1f, 0.2f };

    @Mock
    private PlayerCharacterRepository repository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PlayerCharacterVectorSearchPort vectorSearchPort;

    @Mock
    private EmbeddingPort embeddingPort;

    @Mock
    private StoragePort storagePort;

    @InjectMocks
    private UpdatePlayerCharacterHandler handler;

    @Test
    void shouldThrowExceptionWhenTheNameIsBlank() {

        // given
        var command = updateCommand(UUID.randomUUID(), "", "Brave.", "Tall.", CharacterClass.PALADIN);

        // then
        assertThrows(BusinessRuleViolationException.class, () -> handler.handle(command));
    }

    @Test
    void shouldThrowExceptionWhenThePersonalityIsBlank() {

        // given
        var command = updateCommand(UUID.randomUUID(), "Volin", "", "Tall.", CharacterClass.PALADIN);

        // then
        assertThrows(BusinessRuleViolationException.class, () -> handler.handle(command));
    }

    @Test
    void shouldThrowExceptionWhenThePhysicalDescriptionIsBlank() {

        // given
        var command = updateCommand(UUID.randomUUID(), "Volin", "Brave.", "", CharacterClass.PALADIN);

        // then
        assertThrows(BusinessRuleViolationException.class, () -> handler.handle(command));
    }

    @Test
    void shouldThrowExceptionWhenTheCharacterClassIsNull() {

        // given
        var command = updateCommand(UUID.randomUUID(), "Volin", "Brave.", "Tall.", null);

        // then
        assertThrows(BusinessRuleViolationException.class, () -> handler.handle(command));
    }

    @Test
    void shouldApplyEveryUpdatedFieldWhenTheCharacterIsUpdated() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacterWithId();
        var command = updateCommand(character.getPublicId(), "Volin the Bold", "Reckless.", "Short.",
                CharacterClass.ROGUE);

        when(repository.findByPublicId(character.getPublicId())).thenReturn(Optional.of(character));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.findById(character.getPlayerId())).thenReturn(Optional.of(UserFixture.playerWithId()));

        // when
        handler.execute(command);

        // then
        assertThat(character.getName()).isEqualTo("Volin the Bold");
        assertThat(character.getPersonality()).isEqualTo("Reckless.");
        assertThat(character.getPhysicalDescription()).isEqualTo("Short.");
        assertThat(character.getCharacterClass()).isEqualTo(CharacterClass.ROGUE);
        assertThat(character.getUiImagePositionX()).isEqualTo(0.25);
        assertThat(character.getUiImagePositionY()).isEqualTo(0.75);
    }

    @Test
    void shouldReindexWithTheUpdatedDescriptionWhenTheCharacterIsUpdated() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacterWithId();
        var command = updateCommand(character.getPublicId(), "Volin the Bold", "Reckless.", "Short.",
                CharacterClass.ROGUE);

        when(repository.findByPublicId(character.getPublicId())).thenReturn(Optional.of(character));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(embeddingPort.embed(any())).thenReturn(VECTOR);
        when(userRepository.findById(character.getPlayerId())).thenReturn(Optional.of(UserFixture.playerWithId()));

        // when
        handler.execute(command);

        // then
        var embedded = ArgumentCaptor.forClass(String.class);
        verify(embeddingPort).embed(embedded.capture());

        assertThat(embedded.getValue()).isEqualTo("Volin the Bold: ROGUE; Reckless.; Short.");

        verify(vectorSearchPort).upsert(character.getPublicId(), VECTOR);
    }

    @Test
    void shouldThrowExceptionWhenTheCharacterIsNotFound() {

        // given
        var characterId = UUID.randomUUID();
        var command = updateCommand(characterId, "Volin", "Brave.", "Tall.", CharacterClass.PALADIN);

        when(repository.findByPublicId(characterId)).thenReturn(Optional.empty());

        // then
        assertThrows(NotFoundException.class, () -> handler.execute(command));

        verify(repository, never()).save(any());
        verify(vectorSearchPort, never()).upsert(any(), any());
    }

    @Test
    void shouldThrowExceptionWhenTheOwnerIsNotFound() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacterWithId();
        var command = updateCommand(character.getPublicId(), "Volin", "Brave.", "Tall.", CharacterClass.PALADIN);

        when(repository.findByPublicId(character.getPublicId())).thenReturn(Optional.of(character));
        when(userRepository.findById(character.getPlayerId())).thenReturn(Optional.empty());

        // then
        assertThrows(NotFoundException.class, () -> handler.execute(command));

        verify(repository, never()).save(any());
        verify(vectorSearchPort, never()).upsert(any(), any());
    }

    private UpdatePlayerCharacter updateCommand(
            UUID characterId,
            String name,
            String personality,
            String physicalDescription,
            CharacterClass characterClass) {

        return new UpdatePlayerCharacter(
                characterId, name, characterClass, personality, physicalDescription, 0.25, 0.75);
    }
}
