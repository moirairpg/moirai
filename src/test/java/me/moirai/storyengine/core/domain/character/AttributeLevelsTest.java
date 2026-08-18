package me.moirai.storyengine.core.domain.character;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import me.moirai.storyengine.common.enums.CharacterAttribute;
import me.moirai.storyengine.common.exception.BusinessRuleViolationException;

public class AttributeLevelsTest {

    @Test
    public void shouldThrowExceptionWhenLevelIsBelowZero() {

        // then
        assertThrows(BusinessRuleViolationException.class,
                () -> new AttributeLevels(-1, 0, 2, 0, 0, 1));
    }

    @Test
    public void shouldThrowExceptionWhenLevelIsAboveFive() {

        // given
        var levelAboveRange = 6;

        // then
        assertThrows(BusinessRuleViolationException.class,
                () -> new AttributeLevels(levelAboveRange, 0, 2, 0, 0, 1));
    }

    @Test
    public void shouldReturnLevelsAsMapWhenBuilt() {

        // given
        var levels = new AttributeLevels(3, 0, 2, 0, 0, 1);

        // when
        var map = levels.asMap();

        // then
        assertThat(map)
                .containsEntry(CharacterAttribute.STRENGTH, 3)
                .containsEntry(CharacterAttribute.AGILITY, 0)
                .containsEntry(CharacterAttribute.VIGOR, 2)
                .containsEntry(CharacterAttribute.INTELLIGENCE, 0)
                .containsEntry(CharacterAttribute.AWARENESS, 0)
                .containsEntry(CharacterAttribute.CHARISMA, 1);
    }
}
