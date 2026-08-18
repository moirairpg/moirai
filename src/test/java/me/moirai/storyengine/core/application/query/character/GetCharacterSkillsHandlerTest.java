package me.moirai.storyengine.core.application.query.character;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.storyengine.common.enums.CharacterSkill;
import me.moirai.storyengine.core.port.inbound.character.GetCharacterSkills;

@ExtendWith(MockitoExtension.class)
public class GetCharacterSkillsHandlerTest {

    @InjectMocks
    private GetCharacterSkillsHandler handler;

    @Test
    public void shouldReturnAllEighteenSkillsWithTheCreationRules() {

        // given
        var query = new GetCharacterSkills();

        // when
        var result = handler.execute(query);

        // then
        assertThat(result.skills()).hasSize(CharacterSkill.values().length);
        assertThat(result.skills().getFirst().name()).isEqualTo("ATHLETICS");
        assertThat(result.skills().getFirst().label()).isEqualTo("Athletics");
        assertThat(result.skills().getFirst().attribute()).isEqualTo("STRENGTH");
        assertThat(result.maxLevel()).isEqualTo(4);
        assertThat(result.creation().points()).isEqualTo(4);
        assertThat(result.creation().levelCap()).isEqualTo(2);
        assertThat(result.creation().favoredCost()).isEqualTo(1);
        assertThat(result.creation().offClassCost()).isEqualTo(2);
        assertThat(result.creation().signatureStartingLevel()).isEqualTo(1);
    }
}
