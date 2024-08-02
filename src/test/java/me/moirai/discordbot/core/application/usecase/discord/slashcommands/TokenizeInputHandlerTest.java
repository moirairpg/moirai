package me.moirai.discordbot.core.application.usecase.discord.slashcommands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.discordbot.core.domain.port.TokenizerPort;

@ExtendWith(MockitoExtension.class)
public class TokenizeInputHandlerTest {

    private static final String TOKENIZATION_BASE_RESPONSE = "**Characters:** %s\n**Tokens:** %s\n**Token IDs:** %s (contains %s total tokens).";

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

        String expectedResponse = String.format(TOKENIZATION_BASE_RESPONSE,
                textToBeTokenized.length(), tokens, Arrays.toString(tokenIds), tokenCount);

        when(tokenizerPort.tokenize(anyString())).thenReturn(tokens);
        when(tokenizerPort.getTokensIdsFrom(anyString())).thenReturn(tokenIds);
        when(tokenizerPort.getTokenCountFrom(anyString())).thenReturn(tokenCount);

        TokenizeInput useCase = TokenizeInput.build(textToBeTokenized);

        // When
        String result = handler.execute(useCase);

        // Then
        assertThat(result)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(expectedResponse);
    }

    @Test
    public void tokenizeCommand_whenEncodingErrorIsThrown_thenReturnErrorOutput() throws UnsupportedEncodingException {

        // Given
        String textToBeTokenized = "This is some text.";
        String expectedResponse = "There was an error tokenizing the input.";

        when(tokenizerPort.tokenize(anyString())).thenThrow(UnsupportedEncodingException.class);

        TokenizeInput useCase = TokenizeInput.build(textToBeTokenized);

        // When
        String result = handler.execute(useCase);

        // Then
        assertThat(result)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(expectedResponse);
    }
}
