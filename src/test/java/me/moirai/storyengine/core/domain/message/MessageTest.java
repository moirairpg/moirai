package me.moirai.storyengine.core.domain.message;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import me.moirai.storyengine.common.exception.BusinessRuleViolationException;

public class MessageTest {

    @Test
    public void shouldRecordTheAuthorWhenAPlayerMessageIsBuilt() {

        // when
        var message = MessageFixture.userMessage()
                .authorId(1111L)
                .authorCharacterId(4L)
                .authorCharacterName("Volin Habar")
                .build();

        // then
        assertThat(message.getAuthorId()).isEqualTo(1111L);
        assertThat(message.getAuthorCharacterId()).isEqualTo(4L);
        assertThat(message.getAuthorCharacterName()).isEqualTo("Volin Habar");
    }

    @Test
    public void shouldRecordNoAuthorWhenANarratorMessageIsBuilt() {

        // when
        var message = MessageFixture.assistantMessage().build();

        // then
        assertThat(message.getAuthorId()).isNull();
        assertThat(message.getAuthorCharacterId()).isNull();
        assertThat(message.getAuthorCharacterName()).isNull();
    }

    @Test
    public void shouldReplaceTheRecordedNameWhenTheAuthorCharacterIsRenamed() {

        // given
        var message = MessageFixture.userMessage()
                .authorCharacterName("Volin Habar")
                .build();

        // when
        message.renameAuthorCharacter("Volin the Bold");

        // then
        assertThat(message.getAuthorCharacterName()).isEqualTo("Volin the Bold");
    }

    @Test
    public void shouldThrowExceptionWhenTheNewAuthorCharacterNameIsBlank() {

        // given
        var message = MessageFixture.userMessage().build();

        // then
        assertThrows(BusinessRuleViolationException.class, () -> message.renameAuthorCharacter(EMPTY));
    }
}
