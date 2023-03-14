package es.thalesalv.chatrpg.application.service.tokenizer;

import java.nio.file.Path;

import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;

@Component
public class TokenizerService {
    private final HuggingFaceTokenizer tokenizer;

    public TokenizerService() {
        try {
            tokenizer = HuggingFaceTokenizer.newInstance(Path.of(ResourceUtils.getURL("classpath:tokenizer.json").toURI()));
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
