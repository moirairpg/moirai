package es.thalesalv.chatrpg.application.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;

@Component
public class TokenizerService {

    private static final String PIPE = "|";
    private static final String TOKEN_DIVIDER = "Ġ";
    private static final String TOKENIZER_FILE_PATH = "tokenizer.json";
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenizerService.class);
    private final HuggingFaceTokenizer tokenizer;

    public TokenizerService() {

        try {
            tokenizer = HuggingFaceTokenizer.newInstance(new ClassPathResource(TOKENIZER_FILE_PATH).getInputStream(),
                    null);
        } catch (Exception e) {
            LOGGER.error("Error initializing tokenizer", e);
            throw new RuntimeException("Failed to initialize tokenizer", e);
        }
    }

    public long[] toTokenIds(String text) {

        return tokenizer.encode(new String[] { text }, false)
                .getIds();
    }

    public long[] toTokenIds(String[] texts) {

        return tokenizer.encode(texts, false)
                .getIds();
    }

    public int countTokens(String text) {

        return toTokenIds(text).length;
    }

    public int countTokens(String[] texts) {

        return toTokenIds(texts).length;
    }

    public String tokenize(String text) {

        final List<String> tokens = tokenizer.tokenize(text);
        tokens.replaceAll(a -> a.replaceAll(TOKEN_DIVIDER, PIPE));
        return String.join(StringUtils.EMPTY, tokens);
    }
}
