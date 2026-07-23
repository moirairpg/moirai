package me.moirai.storyengine.core.application.query.character;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

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
    public void shouldReturnEveryCharacterClassWhenQueried() {

        // given
        var query = new GetCharacterClasses();

        var expectedNames = Arrays.stream(CharacterClass.values())
                .map(CharacterClass::name)
                .toList();

        // when
        var result = handler.handle(query);

        // then
        assertThat(result)
                .extracting(CharacterClassResult::name)
                .containsExactlyElementsOf(expectedNames);
    }

    @Test
    public void shouldReturnTheLabelOfEachClassWhenQueried() {

        // given
        var query = new GetCharacterClasses();

        // when
        var result = handler.handle(query);

        // then
        assertThat(result).allSatisfy(characterClass -> {
            assertThat(characterClass.label()).isNotBlank();
            assertThat(characterClass.label()).isEqualTo(CharacterClass.valueOf(characterClass.name()).getLabel());
        });
    }

    @Test
    public void shouldReturnNamesThatMapBackToTheEnumWhenQueried() {

        // given
        var query = new GetCharacterClasses();

        // when
        var result = handler.handle(query);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result)
                .allSatisfy(characterClass -> assertThat(CharacterClass.valueOf(characterClass.name())).isNotNull());
    }
}
