package me.moirai.storyengine.core.domain.character;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import me.moirai.storyengine.common.enums.CharacterSkill;
import me.moirai.storyengine.common.exception.BusinessRuleViolationException;

public class SkillLevelsTest {

    @Test
    public void shouldThrowExceptionWhenLevelIsBelowZero() {

        // then
        assertThrows(BusinessRuleViolationException.class,
                () -> new SkillLevels(-1, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 1));
    }

    @Test
    public void shouldThrowExceptionWhenLevelIsAboveFour() {

        // given
        var levelAboveRange = 5;

        // then
        assertThrows(BusinessRuleViolationException.class,
                () -> new SkillLevels(levelAboveRange, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 1));
    }

    @Test
    public void shouldReturnPoolLevelsAsMapWhenBuilt() {

        // given
        var levels = new SkillLevels(0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 1);

        // when
        var map = levels.asMap();

        // then
        assertThat(map)
                .hasSize(21)
                .containsEntry(CharacterSkill.ENDURANCE, 2)
                .containsEntry(CharacterSkill.PERSUASION, 2)
                .containsEntry(CharacterSkill.MELEE, 0)
                .containsEntry(CharacterSkill.ATHLETICS, 0);
    }
}
