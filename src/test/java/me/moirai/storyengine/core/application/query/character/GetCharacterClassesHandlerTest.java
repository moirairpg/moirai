package me.moirai.storyengine.core.application.query.character;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.storyengine.common.enums.CharacterClass;
import me.moirai.storyengine.core.port.inbound.character.CharacterClassResult;
import me.moirai.storyengine.core.port.inbound.character.GetCharacterClasses;

@ExtendWith(MockitoExtension.class)
public class GetCharacterClassesHandlerTest {

    @InjectMocks
    private GetCharacterClassesHandler handler;

    @Test
    public void shouldReturnAllNineClassesWithSignatureAndFavoredSkills() {

        // given
        var query = new GetCharacterClasses();

        // when
        var result = handler.execute(query);

        // then
        assertThat(result).hasSize(CharacterClass.values().length);

        var druid = result.stream()
                .filter(characterClass -> characterClass.name().equals("DRUID"))
                .findFirst()
                .orElseThrow();

        assertThat(druid.label()).isEqualTo("Druid");
        assertThat(druid.signatureSkill().name()).isEqualTo("COMMUNE");
        assertThat(druid.signatureSkill().label()).isEqualTo("Commune");
        assertThat(druid.signatureSkill().attribute()).isEqualTo("AWARENESS");
        assertThat(druid.favoredSkills())
                .containsExactly("SURVIVAL", "INTUITION", "RESTORATION", "PERCEPTION");
    }

    @Test
    public void shouldReturnTheClassesInEnumOrderWhenListingThem() {

        // given
        var query = new GetCharacterClasses();

        // when
        var result = handler.execute(query);

        // then
        assertThat(result)
                .extracting(CharacterClassResult::name)
                .containsExactly(
                        "BARD",
                        "RANGER",
                        "BARBARIAN",
                        "PALADIN",
                        "MAGE",
                        "ROGUE",
                        "WITCH",
                        "CLERIC",
                        "DRUID");
    }
}
