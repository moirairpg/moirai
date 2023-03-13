package es.thalesalv.chatrpg.application.service.tokenizer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TokenizerServiceTest {
    private static final String text = "This is a test.";
    private static final long[] textIds = {1212, 318, 257, 1332, 13}; // verified on OAI website
    private static final String[] texts = {"This is a test.", "This is only a test."};
    private static final long[] textsIds = {1212, 318, 257, 1332, 13, 1212, 318, 691, 257, 1332, 13}; // verified on OAI website

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
}