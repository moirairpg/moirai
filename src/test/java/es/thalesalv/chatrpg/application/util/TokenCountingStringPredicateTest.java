package es.thalesalv.chatrpg.application.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenCountingStringPredicateTest {
    private static final String TEN_TOKENS = "This is a ten token string. Ten tokens.";
    private static final String HUNDRED_TOKENS = "This is a one hundred token string. There are many possible strings that occupy exactly one hundred tokens, but this is the one I have chosen. If you don't like it, feel free to change it, but in that case please ensure that it is exactly one hundred tokens in length. Ninety-nine tokens would fall short of the requirement, so please be exacting and thorough. I'd recommend checking your string on: http://platform.openai.com/tokenizer. Good luck";

    private TokenCountingStringPredicate predicate;

    @BeforeEach
    void init() {
        predicate = new TokenCountingStringPredicate(150);
    }

    @AfterEach
    void destroy() {
        predicate = null;
    }

    @Test
    void testTest() {
        assertTrue(predicate.test(HUNDRED_TOKENS));
        assertFalse(predicate.test(HUNDRED_TOKENS));
        assertTrue(predicate.test(TEN_TOKENS));
    }

    @Test
    void reserve() {
        predicate.reserve(40);
        assertTrue(predicate.test(HUNDRED_TOKENS));
        assertFalse(predicate.test(HUNDRED_TOKENS));
        assertTrue(predicate.test(TEN_TOKENS));
        assertFalse(predicate.test(TEN_TOKENS));
    }
}