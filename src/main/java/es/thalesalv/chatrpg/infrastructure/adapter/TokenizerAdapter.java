package es.thalesalv.chatrpg.infrastructure.adapter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import es.thalesalv.chatrpg.core.application.port.TokenizerPort;

@Component
public class TokenizerAdapter implements TokenizerPort {

    private final HuggingFaceTokenizer tokenizer;

    private static final String TOKEN_DELIMITER = "|";
    private static final String SPECIAL_SPACE_TOKEN = "Ġ";
    private static final String TOKENIZER_FILE_PATH = "tokenizer.json";
    private static final String ISO_8859_1_ENCODING = "ISO-8859-1";

    public TokenizerAdapter() throws IOException {

        Map<String, String> tokenizerOptions = new HashMap<>();
        tokenizerOptions.put("addSpecialTokens", "false");

        tokenizer = HuggingFaceTokenizer.newInstance(new ClassPathResource(TOKENIZER_FILE_PATH).getInputStream(),
                tokenizerOptions);
    }

    @Override
    public long[] getTokensIdsFrom(String text) {

        return tokenizer.encode(new String[] { text }, false)
                .getIds();
    }

    @Override
    public long[] getTokensIdsFrom(String[] texts) {

        return tokenizer.encode(texts, false).getIds();
    }

    @Override
    public int getTokenCountFrom(String text) {

        return getTokensIdsFrom(text).length;
    }

    @Override
    public int getTokenCountFrom(String[] texts) {

        return getTokensIdsFrom(texts).length;
    }

    @Override
    public String tokenize(String text) throws UnsupportedEncodingException {

        List<String> tokenList = tokenizer.tokenize(text);
        tokenList.replaceAll(a -> a.replaceAll(SPECIAL_SPACE_TOKEN, StringUtils.SPACE));
        String tokenized = String.join(TOKEN_DELIMITER, tokenList);

        return new String(tokenized.getBytes(ISO_8859_1_ENCODING));
    }
}
