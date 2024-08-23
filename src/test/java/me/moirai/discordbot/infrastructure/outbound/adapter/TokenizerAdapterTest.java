package me.moirai.discordbot.infrastructure.outbound.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import me.moirai.discordbot.core.application.usecase.discord.slashcommands.TokenizeResult;

public class TokenizerAdapterTest {

    private TokenizerAdapter tokenizer;

    @BeforeEach
    void setUp() throws IOException {

        tokenizer = new TokenizerAdapter();
    }

    @Test
    public void extractTokenIdsFromSingleInput() {

        // Given
        long[] expectedTokenIds = { 1212, 318, 257, 1332, 13 };
        String textToTokenize = "This is a test.";

        // When
        long[] returnedTokenIds = tokenizer.getTokensIdsFrom(textToTokenize);

        // Then
        assertThat(expectedTokenIds).isEqualTo(returnedTokenIds);
    }

    @Test
    public void countTokensFromSingleInput() {

        // Given
        String textToTokenize = "This is a test.";
        int expectedTokenCount = 5;

        // When
        int tokenCount = tokenizer.getTokenCountFrom(textToTokenize);

        // Then
        assertThat(expectedTokenCount).isEqualTo(tokenCount);
    }

    @Test
    public void countTokens_whenNullText_returnsZero() {

        // Given
        String textToTokenize = null;
        int expectedTokenCount = 0;

        // When
        int tokenCount = tokenizer.getTokenCountFrom(textToTokenize);

        // Then
        assertThat(expectedTokenCount).isEqualTo(tokenCount);
    }

    @Test
    public void countTokens_whenEmptyText_returnsZero() {

        // Given
        String textToTokenize = "";
        int expectedTokenCount = 0;

        // When
        int tokenCount = tokenizer.getTokenCountFrom(textToTokenize);

        // Then
        assertThat(expectedTokenCount).isEqualTo(tokenCount);
    }

    @Test
    public void tokenizeEnglishText() {

        // Given
        String englishText = "This is a test in English.";
        String expectedTokenizedText = "This| is| a| test| in| English|.";

        // When
        String tokenizedText = tokenizer.getTokens(englishText);

        // Then
        assertThat(expectedTokenizedText).isEqualTo(tokenizedText);
    }

    @Test
    public void tokenizeNonEnglishText() {

        // Given
        String englishText = "Este é um teste num idioma não-inglês.";
        String expectedTokenizedText = "E|ste| é| um| test|e| num| idi|oma| n|ão|-|ing|l|ê|s|.";

        // When
        String tokenizedText = tokenizer.getTokens(englishText);

        // Then
        assertThat(expectedTokenizedText).isEqualTo(tokenizedText);
    }

    @Test
    public void tokenizeForCompleteOutput() {

        // Given
        String textToTokenize = "This is a test.";
        long[] expectedTokenIds = { 1212, 318, 257, 1332, 13 };
        String expectedTokenizedText = "This| is| a| test|.";
        int expectedTokenCount = 5;
        int length = textToTokenize.length();

        // When
        TokenizeResult result = tokenizer.tokenize(textToTokenize);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCharacterCount()).isEqualTo(length);
        assertThat(result.getTokenIds()).isEqualTo(expectedTokenIds);
        assertThat(result.getTokens()).isEqualTo(expectedTokenizedText);
        assertThat(result.getTokenCount()).isEqualTo(expectedTokenCount);
    }
}
