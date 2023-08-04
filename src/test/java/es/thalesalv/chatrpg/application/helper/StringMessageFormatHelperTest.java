package es.thalesalv.chatrpg.application.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import es.thalesalv.chatrpg.application.util.StringProcessor;
import es.thalesalv.chatrpg.application.util.StringProcessors;
import es.thalesalv.chatrpg.domain.model.EventData;
import es.thalesalv.chatrpg.domain.model.bot.Persona;
import es.thalesalv.chatrpg.testutils.EventDataUtils;
import es.thalesalv.chatrpg.testutils.TextMessageUtils;
import net.dv8tion.jda.api.entities.SelfUser;

@ExtendWith(MockitoExtension.class)
public class StringMessageFormatHelperTest {

    @InjectMocks
    private StringMessageFormatHelper stringMessageFormatHelper;

    @Test
    public void formatMessagesTest() {

        final List<String> messages = TextMessageUtils.createChat();
        final EventData eventData = EventDataUtils.buildEventData();
        final StringProcessor stringProcessor = new StringProcessor();
        final Persona persona = eventData.getChannelDefinitions()
                .getChannelConfig()
                .getPersona();

        stringProcessor.addRule(StringProcessors.replacePlaceholderWithPersona(persona));

        final List<String> chatMessages = stringMessageFormatHelper.formatMessages(messages, eventData,
                stringProcessor);

        assertEquals("This is a test persona. My name is ChatRPG", chatMessages.get(0));
        assertEquals("John said: Hello", chatMessages.get(1));
        assertEquals("Martha said: Hello, how are you?", chatMessages.get(2));
        assertEquals("this is a bump", chatMessages.get(3));
        assertEquals("John said: I am fine. How is the bot?", chatMessages.get(4));

        assertEquals("ChatRPG said: As an AI language model, I cannot be well or bad. But thank you for asking.",
                chatMessages.get(5));

        assertEquals("Martha says: yeah right", chatMessages.get(6));
        assertEquals("My name is ChatRPG and this is a nudge", chatMessages.get(7));
    }

    @Test
    public void testChatifyMessage() {

        final SelfUser bot = mock(SelfUser.class);
        final List<String> messages = TextMessageUtils.createChat();
        final EventData eventData = EventDataUtils.buildEventData(bot);
        final StringProcessor stringProcessor = new StringProcessor();
        stringProcessor.addRule(s -> Pattern.compile("\\{0\\}")
                .matcher(s)
                .replaceAll(r -> eventData.getChannelDefinitions()
                        .getChannelConfig()
                        .getPersona()
                        .getName()));

        when(bot.getName()).thenReturn("ChatRPG");

        final String result = stringMessageFormatHelper.chatifyMessages(messages, eventData, stringProcessor);
        final String[] lines = result.split("\n");
        assertEquals("John said: Hello", lines[0]);
    }
}
