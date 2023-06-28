package es.thalesalv.chatrpg.application.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.UnsupportedEncodingException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TokenizerServiceTest {

    private static final String nonEnglishText = "Série de tokens num idioma não-inglês.";
    private static final String text = "This is a test.";
    private static final long[] textIds = { 1212, 318, 257, 1332, 13 };
    private static final String[] texts = { "This is a test.", "This is only a test." };
    private static final long[] textsIds = { 1212, 318, 257, 1332, 13, 1212, 318, 691, 257, 1332, 13 };

    private final TokenizerService service = new TokenizerService();

    @BeforeEach
    void setUp() {

    }

    @Test
    void toTokenIds() {

        assertArrayEquals(textIds, service.toTokenIds(text));
        assertArrayEquals(textsIds, service.toTokenIds(texts));
    }

    @Test
    void countTokens() {

        assertEquals(textIds.length, service.countTokens(text));
        assertEquals(textsIds.length, service.countTokens(texts));
    }

    @Test
    void tokenize() throws UnsupportedEncodingException {

        final String tokensString = service.tokenize(text);
        Assertions.assertEquals("This| is| a| test|.", tokensString);
    }

    @Test
    void tokenizeNonEnglish() throws UnsupportedEncodingException {

        final String tokensString = service.tokenize(nonEnglishText);
        Assertions.assertEquals("S|é|rie| de| tokens| num| idi|oma| n|ão|-|ing|l|ê|s|.", tokensString);
    }
}