package me.moirai.discordbot.core.domain.channelconfig;

import static me.moirai.discordbot.core.domain.channelconfig.Moderation.STRICT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import me.moirai.discordbot.common.exception.BusinessRuleViolationException;

public class ModerationTest {

    @Test
    public void convertFromString() {

        // Given
        String moderationString = "strict";

        // When
        Moderation moderation = Moderation.fromString(moderationString);

        // Then
        assertThat(moderation).isEqualTo(STRICT);
    }

    @Test
    public void errorWhenModerationIsInvalid() {

        // Given
        String moderationString = "ahahahaha";

        // Then
        assertThrows(BusinessRuleViolationException.class, () -> Moderation.fromString(moderationString));
    }

    @Test
    public void errorWhenModerationIsNull() {

        // Given
        String moderationString = null;

        // Then
        assertThrows(BusinessRuleViolationException.class, () -> Moderation.fromString(moderationString));
    }
}
