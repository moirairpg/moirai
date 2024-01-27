package es.thalesalv.chatrpg.infrastructure.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    void extractTokenIdsFromMultipleInputs() {

        // Given
        long[] expectedTokenIds = { 1212, 318, 257, 1332, 13, 1212, 318, 691, 257, 1332, 13 };
        String[] textsToTokenize = { "This is a test.", "This is only a test." };

        // When
        long[] returnedTokenIds = tokenizer.getTokensIdsFrom(textsToTokenize);

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
    public void countTokensFromMultipleInputs() {

        // Given
        String[] textsToTokenize = { "This is a test.", "This is only a test." };
        int expectedTokenCount = 11;

        // When
        int tokenCount = tokenizer.getTokenCountFrom(textsToTokenize);

        // Then
        assertThat(expectedTokenCount).isEqualTo(tokenCount);
    }

    @Test
    public void tokenizeEnglishText() throws UnsupportedEncodingException {

        // Given
        String englishText = "This is a test in English.";
        String expectedTokenizedText = "This| is| a| test| in| English|.";

        // When
        String tokenizedText = tokenizer.tokenize(englishText);

        // Then
        assertThat(expectedTokenizedText).isEqualTo(tokenizedText);
    }

    @Test
    public void tokenizeNonEnglishText() throws UnsupportedEncodingException {

        // Given
        String englishText = "Este é um teste num idioma não-inglês.";
        String expectedTokenizedText = "E|ste| é| um| test|e| num| idi|oma| n|ão|-|ing|l|ê|s|.";

        // When
        String tokenizedText = tokenizer.tokenize(englishText);

        // Then
        assertThat(expectedTokenizedText).isEqualTo(tokenizedText);
    }
}
