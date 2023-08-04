package es.thalesalv.chatrpg.application.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import es.thalesalv.chatrpg.domain.model.bot.Persona;
import es.thalesalv.chatrpg.testutils.PersonaTestUtils;

class StringProcessorTest {

    final StringProcessor processor = new StringProcessor();
    final String toTest = "Shel said: [ The man draws his sword. ]";
    final String botMessageTest = "{0} said: I am {0}, it's a pleasure!";

    @Test
    public void test() {

        processor.addRule(s -> Pattern.compile("Selkie")
                .matcher(s)
                .replaceAll(r -> "Janet"));

        assertEquals(toTest, processor.process(toTest));
    }

    @Test
    public void testSimpleName() {

        final Persona persona = PersonaTestUtils.buildSimplePublicPersona();
        final String processed = StringProcessors.replacePlaceholderWithPersona(persona)
                .apply(persona.getPersonality());

        assertEquals("This is a test persona. My name is ChatRPG", processed);
    }

    @Test
    public void testCompositeName() {

        final Persona persona = PersonaTestUtils.buildSimplePublicPersona();
        persona.setName("ChatRPG the DM Bot");

        final String processed = StringProcessors.replacePlaceholderWithPersona(persona)
                .apply(persona.getPersonality());

        assertEquals("This is a test persona. My name is ChatRPG the DM Bot", processed);
    }
}