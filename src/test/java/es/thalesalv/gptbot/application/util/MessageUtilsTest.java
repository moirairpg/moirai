package es.thalesalv.gptbot.application.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import es.thalesalv.gptbot.application.config.Persona;
import es.thalesalv.gptbot.testutils.DiscordMocker;
import es.thalesalv.gptbot.testutils.PersonaBuilder;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;

@SuppressWarnings("all")
@ExtendWith(MockitoExtension.class)
public class MessageUtilsTest {

    @Test
    public void testChatifyMessages() {

        final User bot = DiscordMocker.buildUser();
        final List<String> list = new ArrayList<>();
        list.add("Malaquias said: I am Malaquias.");
        list.add("User said: Hello @Malaquias");
        final String result = MessageUtils.chatifyMessages(bot, list);

        Assertions.assertEquals("User said: Hello", list.get(1));
    }

    @Test
    public void testFormatPersonality() {

        final Persona persona = PersonaBuilder.persona();
        final SelfUser bot = DiscordMocker.buildSelfUser();
        final List<String> list = new ArrayList<>();
        list.add("Malaquias said: I am Malaquias.");
        list.add("User said: Hello @Malaquias");
        MessageUtils.formatPersonality(list, persona, bot);

        Assertions.assertEquals("I am a robot", list.get(0));
    }
}
