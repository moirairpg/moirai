package es.thalesalv.chatrpg.application.service.tokenizer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;

@Component
public class TokenizerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenizerService.class);

    private final HuggingFaceTokenizer tokenizer;

    public TokenizerService() {
        try {
            tokenizer = HuggingFaceTokenizer.newInstance(getFileFromJar("tokenizer.json"));
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

    public Path getFileFromJar(String filePathInJar) throws IOException {
        
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePathInJar);
        Path tempDirPath = Files.createTempDirectory("temp-dir");
        Path tempFilePath = Files.write(tempDirPath.resolve("temp-file"), inputStream.readAllBytes(), StandardOpenOption.CREATE);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Files.walk(tempDirPath)
                     .sorted(Comparator.reverseOrder())
                     .map(Path::toFile)
                     .forEach(File::delete);
            } catch (IOException e) {
                LOGGER.error("Error retrieving path of tokenizer file", e);
                throw new RuntimeException("Error retrieving path of tokenizer file", e);
            }
        }));

        return tempFilePath;
    }
}
