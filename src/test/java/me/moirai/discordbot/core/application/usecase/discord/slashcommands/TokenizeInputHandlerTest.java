package me.moirai.discordbot.core.application.usecase.discord.slashcommands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.discordbot.core.domain.port.TokenizerPort;

@ExtendWith(MockitoExtension.class)
public class TokenizeInputHandlerTest {

    @Mock
    private TokenizerPort tokenizerPort;

    @InjectMocks
    private TokenizeInputHandler handler;

    @Test
    public void tokenizeCommand_whenInputIsSupplied_thenReturnTokenizedOutput() throws UnsupportedEncodingException {

        // Given
        String textToBeTokenized = "This is some text.";
        String tokens = "This| is| some| text|.";
        long[] tokenIds = { 1212, 318, 617, 2420, 13 };
        int tokenCount = 5;

        TokenizeResult expectedAdapterResult = TokenizeResult.builder()
                .tokens(tokens)
                .tokenCount(tokenCount)
                .tokenIds(tokenIds)
                .characterCount(textToBeTokenized.length())
                .build();

        when(tokenizerPort.tokenize(anyString())).thenReturn(expectedAdapterResult);

        TokenizeInput useCase = TokenizeInput.build(textToBeTokenized);

        // When
        Optional<TokenizeResult> result = handler.execute(useCase);

        // Then
        assertThat(result)
                .isNotNull()
                .isNotEmpty()
                .contains(expectedAdapterResult);
    }

    @Test
    public void tokenizeCommand_whenEncodingErrorIsThrown_thenReturnErrorOutput() throws UnsupportedEncodingException {

        // Given
        String textToBeTokenized = "This is some text.";

        TokenizeInput useCase = TokenizeInput.build(textToBeTokenized);

        // When
        Optional<TokenizeResult> result = handler.execute(useCase);

        // Then
        assertThat(result)
                .isNotNull()
                .isEmpty();
    }
}
