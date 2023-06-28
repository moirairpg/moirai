package es.thalesalv.chatrpg.application.util;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class StringProcessorTest {

    String toTest = "Shel said: [ The man draws his sword. ]";

    @Test
    public void test() {

        StringProcessor processor = new StringProcessor();
        processor.addRule(s -> Pattern.compile("Selkie")
                .matcher(s)
                .replaceAll(r -> "Janet"));

        assertEquals(toTest, processor.process(toTest));
    }

}