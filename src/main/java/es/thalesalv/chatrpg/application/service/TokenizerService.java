package es.thalesalv.chatrpg.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;

@Component
public class TokenizerService {

    private static final String TOKENIZER_FILE_PATH = "tokenizer.json";
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenizerService.class);

    private final HuggingFaceTokenizer tokenizer;

    public TokenizerService() {
        try {
            tokenizer = HuggingFaceTokenizer.newInstance(new ClassPathResource(TOKENIZER_FILE_PATH).getInputStream(), null);
        } catch (Exception e) {
            LOGGER.error("Error initializing tokenizer", e);
            throw new RuntimeException("Failed to initialize tokenizer", e);
        }
    }

    public long[] toTokenIds(String text) {
        return tokenizer.encode(new String[]{text}, false).getIds();
    }

    public long[] toTokenIds(String[] texts) {
        return tokenizer.encode(texts, false).getIds();
    }

    public int countTokens(String text) {
        return toTokenIds(text).length;
    }

    public int countTokens(String[] texts) {
        return toTokenIds(texts).length;
    }

    public long[] tokenize(String text) {
        return toTokenIds(text);
    }

    public long[] tokenize(String[] texts) {
        return toTokenIds(texts);
    }
}
