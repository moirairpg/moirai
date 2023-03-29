package es.thalesalv.chatrpg.application.util;

import es.thalesalv.chatrpg.application.service.TokenizerService;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public class TokenCountingStringPredicate implements Predicate<String> {

    private final TokenizerService tokenizerService = new TokenizerService();
    private final int limit;
    private final AtomicInteger tokenCount = new AtomicInteger(0);

    public TokenCountingStringPredicate(final int limit) {

        this.limit = limit;
    }

    @Override
    public boolean test(final String string) {

        final int tokens = tokenizerService.countTokens(string);
        boolean result = tokenCount.addAndGet(tokens) <= limit;
        if (!result)
            tokenCount.addAndGet(-tokens);
        return result;
    }

    public void reserve(final int quantity) {

        tokenCount.addAndGet(quantity);
    }

    public void reserve(final String text) {
        final int tokens = tokenizerService.countTokens(text);
        tokenCount.addAndGet(tokens);
    }

    public int getCount() {
        return tokenCount.get();
    }
}
