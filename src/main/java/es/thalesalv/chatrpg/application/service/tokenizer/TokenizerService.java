package es.thalesalv.chatrpg.application.service.tokenizer;

import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;

import java.nio.file.Path;

public class TokenizerService {
    private final HuggingFaceTokenizer tokenizer;

    public TokenizerService() {
        try {
            tokenizer = HuggingFaceTokenizer.newInstance(Path.of(ClassLoader.getSystemResource("tokenizer.json").toURI()));
        } catch (Exception e) {
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
}
