package me.moirai.storyengine.core.application.query.character;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.storyengine.core.port.inbound.character.CharacterAttributeResult;
import me.moirai.storyengine.core.port.inbound.character.GetCharacterAttributes;

@ExtendWith(MockitoExtension.class)
public class GetCharacterAttributesHandlerTest {

    @InjectMocks
    private GetCharacterAttributesHandler handler;

    @Test
    public void shouldReturnAllSixAttributesWithTheCreationRules() {

        // given
        var query = new GetCharacterAttributes();

        // when
        var result = handler.execute(query);

        // then
        assertThat(result.attributes())
                .extracting(CharacterAttributeResult::name)
                .containsExactly("STRENGTH", "AGILITY", "VIGOR", "INTELLIGENCE", "AWARENESS", "CHARISMA");
        assertThat(result.attributes().getFirst().label()).isEqualTo("Strength");
        assertThat(result.maxLevel()).isEqualTo(5);
        assertThat(result.creation().points()).isEqualTo(6);
        assertThat(result.creation().levelCap()).isEqualTo(3);
    }
}
