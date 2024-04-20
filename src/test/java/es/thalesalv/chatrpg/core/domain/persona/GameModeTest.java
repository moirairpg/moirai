package es.thalesalv.chatrpg.core.domain.persona;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import es.thalesalv.chatrpg.common.exception.BusinessRuleViolationException;

public class GameModeTest {

    @Test
    public void gameMode_whenInputIsBlank_thenThrowBusinessRuleViolationException() {

        // Given
        String input = "";

        // Then
        assertThrows(BusinessRuleViolationException.class, () -> GameMode.fromString(input));
    }

    @Test
    public void gameMode_whenInputIsInvalid_thenThrowBusinessRuleViolationException() {

        // Given
        String input = "invalid";

        // Then
        assertThrows(BusinessRuleViolationException.class, () -> GameMode.fromString(input));
    }

    @Test
    public void gameMode_whenInputIsValid_thenGameModeIsReturned() {

        // Given
        String input = "rpg";

        // When
        GameMode gameMode = GameMode.fromString(input);

        // Then
        assertThat(gameMode).isEqualTo(GameMode.RPG);
    }
}
