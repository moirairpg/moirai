package me.moirai.storyengine.core.domain.character;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import me.moirai.storyengine.common.enums.CharacterClass;
import me.moirai.storyengine.common.exception.BusinessRuleViolationException;

public class PlayerCharacterTest {

    @Test
    public void shouldCreateInstanceWhenDataIsValid() {

        // given
        var builder = PlayerCharacter.builder()
                .name("Volin Habar")
                .playerId(1111L)
                .personality("Brave, honorable and disciplined.")
                .physicalDescription("A tall warrior with long black hair.")
                .characterClass(CharacterClass.PALADIN);

        // when
        var character = builder.build();

        // then
        assertThat(character).isNotNull();
        assertThat(character.getPublicId()).isNotNull();
        assertThat(character.getName()).isEqualTo("Volin Habar");
        assertThat(character.getPlayerId()).isEqualTo(1111L);
        assertThat(character.getPersonality()).isEqualTo("Brave, honorable and disciplined.");
        assertThat(character.getPhysicalDescription()).isEqualTo("A tall warrior with long black hair.");
        assertThat(character.getCharacterClass()).isEqualTo(CharacterClass.PALADIN);
    }

    @Test
    public void shouldUpdateNameWhenNewValueIsProvided() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacter().build();

        // when
        character.updateName("New Name");

        // then
        assertThat(character.getName()).isEqualTo("New Name");
    }

    @Test
    public void shouldUpdatePersonalityWhenNewValueIsProvided() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacter().build();

        // when
        character.updatePersonality("New Personality");

        // then
        assertThat(character.getPersonality()).isEqualTo("New Personality");
    }

    @Test
    public void shouldUpdatePhysicalDescriptionWhenNewValueIsProvided() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacter().build();

        // when
        character.updatePhysicalDescription("New Description");

        // then
        assertThat(character.getPhysicalDescription()).isEqualTo("New Description");
    }

    @Test
    public void shouldThrowExceptionWhenNameIsUpdatedToBlank() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacter().build();

        // then
        assertThrows(BusinessRuleViolationException.class, () -> character.updateName(EMPTY));
    }

    @Test
    public void shouldThrowExceptionWhenPersonalityIsUpdatedToBlank() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacter().build();

        // then
        assertThrows(BusinessRuleViolationException.class, () -> character.updatePersonality(EMPTY));
    }

    @Test
    public void shouldThrowExceptionWhenPhysicalDescriptionIsUpdatedToBlank() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacter().build();

        // then
        assertThrows(BusinessRuleViolationException.class,
                () -> character.updatePhysicalDescription(EMPTY));
    }

    @Test
    public void shouldUpdateCharacterClassWhenNewValueIsProvided() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacter().build();

        // when
        character.updateCharacterClass(CharacterClass.MAGE);

        // then
        assertThat(character.getCharacterClass()).isEqualTo(CharacterClass.MAGE);
    }

    @Test
    public void shouldUpdateImageKeyWhenNewValueIsProvided() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacter().build();

        // when
        character.updateImageKey("characters/volin.png");

        // then
        assertThat(character.getImageKey()).isEqualTo("characters/volin.png");
    }

    @Test
    public void shouldThrowExceptionWhenNameIsNull() {

        // given
        var builder = PlayerCharacterFixture.samplePlayerCharacter().name(null);

        // then
        assertThrows(BusinessRuleViolationException.class, builder::build);
    }

    @Test
    public void shouldThrowExceptionWhenNameIsEmpty() {

        // given
        var builder = PlayerCharacterFixture.samplePlayerCharacter().name(EMPTY);

        // then
        assertThrows(BusinessRuleViolationException.class, builder::build);
    }

    @Test
    public void shouldThrowExceptionWhenPlayerIdIsNull() {

        // given
        var builder = PlayerCharacterFixture.samplePlayerCharacter().playerId(null);

        // then
        assertThrows(BusinessRuleViolationException.class, builder::build);
    }

    @Test
    public void shouldThrowExceptionWhenPersonalityIsNull() {

        // given
        var builder = PlayerCharacterFixture.samplePlayerCharacter().personality(null);

        // then
        assertThrows(BusinessRuleViolationException.class, builder::build);
    }

    @Test
    public void shouldThrowExceptionWhenPersonalityIsEmpty() {

        // given
        var builder = PlayerCharacterFixture.samplePlayerCharacter().personality(EMPTY);

        // then
        assertThrows(BusinessRuleViolationException.class, builder::build);
    }

    @Test
    public void shouldThrowExceptionWhenPhysicalDescriptionIsNull() {

        // given
        var builder = PlayerCharacterFixture.samplePlayerCharacter().physicalDescription(null);

        // then
        assertThrows(BusinessRuleViolationException.class, builder::build);
    }

    @Test
    public void shouldThrowExceptionWhenPhysicalDescriptionIsEmpty() {

        // given
        var builder = PlayerCharacterFixture.samplePlayerCharacter().physicalDescription(EMPTY);

        // then
        assertThrows(BusinessRuleViolationException.class, builder::build);
    }

    @Test
    public void shouldThrowExceptionWhenCharacterClassIsNull() {

        // given
        var builder = PlayerCharacterFixture.samplePlayerCharacter().characterClass(null);

        // then
        assertThrows(BusinessRuleViolationException.class, builder::build);
    }

    @Test
    public void shouldThrowExceptionWhenNoFieldIsProvided() {

        // given
        var builder = PlayerCharacter.builder();

        // then
        assertThrows(BusinessRuleViolationException.class, builder::build);
    }

    @Test
    public void shouldRecordDeletedEventWhenCharacterIsDeleted() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacterWithId();

        // when
        character.communicateCharacterDeleted();

        // then
        var events = character.drainEvents();

        assertThat(events).hasSize(1);
        assertThat(events.getFirst()).isInstanceOf(PlayerCharacterDeletedEvent.class);

        var event = (PlayerCharacterDeletedEvent) events.getFirst();

        assertThat(event.getPlayerCharacterId()).isEqualTo(PlayerCharacterFixture.NUMERIC_ID);
        assertThat(event.getPlayerId()).isEqualTo(PlayerCharacterFixture.PLAYER_ID);
    }

    @Test
    public void shouldEmptyRecordedEventsWhenDrained() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacterWithId();
        character.communicateCharacterDeleted();

        // when
        character.drainEvents();

        // then
        assertThat(character.drainEvents()).isEmpty();
    }
}
