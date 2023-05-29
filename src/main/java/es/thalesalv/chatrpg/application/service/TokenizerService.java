package es.thalesalv.chatrpg.application.service;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;

@Component
public class TokenizerService {

    private final HuggingFaceTokenizer tokenizer;

    private static final String PIPE = "|";
    private static final String SPACE_SPECIAL_TOKEN = "Ġ";
    private static final String TOKENIZER_FILE_PATH = "tokenizer.json";
    private static final String ISO_8859_1_ENCODING = "ISO-8859-1";

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenizerService.class);

    public TokenizerService() {

        try {
            final Map<String, String> tokenizerOptions = new HashMap<>();
            tokenizerOptions.put("addSpecialTokens", "false");
            tokenizer = HuggingFaceTokenizer.newInstance(new ClassPathResource(TOKENIZER_FILE_PATH).getInputStream(),
                    tokenizerOptions);
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

    public String tokenize(String text) throws UnsupportedEncodingException {

        final List<String> tokenList = tokenizer.tokenize(text);
        tokenList.replaceAll(a -> a.replaceAll(SPACE_SPECIAL_TOKEN, StringUtils.SPACE));
        final String tokenized = String.join(PIPE, tokenList);

        return new String(tokenized.getBytes(ISO_8859_1_ENCODING));
    }
}
