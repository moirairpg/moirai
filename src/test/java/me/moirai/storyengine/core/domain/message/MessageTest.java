package me.moirai.storyengine.core.domain.message;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import me.moirai.storyengine.common.enums.MessageAuthorRole;
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
    public void shouldRecordNoPlayerWhenANarratorMessageIsBuilt() {

        // when
        var message = MessageFixture.assistantMessage().build();

        // then
        assertThat(message.getAuthorId()).isNull();
        assertThat(message.getAuthorCharacterId()).isNull();
        assertThat(message.getAuthorCharacterName()).isEqualTo("Narrator");
    }

    @Test
    public void shouldThrowExceptionWhenNoSpeakerIsRecorded() {

        // when
        var builder = Message.builder()
                .adventureId(1L)
                .role(MessageAuthorRole.ASSISTANT)
                .content("A door opens.");

        // then
        assertThrows(BusinessRuleViolationException.class, builder::build);
    }

    @Test
    public void shouldReplaceTheContentWhenTheMessageIsEdited() {

        // given
        var message = MessageFixture.userMessage()
                .content("Hello")
                .build();

        // when
        message.updateContent("Hello there");

        // then
        assertThat(message.getContent()).isEqualTo("Hello there");
    }

    @Test
    public void shouldThrowExceptionWhenTheUpdatedContentIsBlank() {

        // given
        var message = MessageFixture.userMessage().build();

        // then
        assertThrows(BusinessRuleViolationException.class, () -> message.updateContent(EMPTY));
    }
}
