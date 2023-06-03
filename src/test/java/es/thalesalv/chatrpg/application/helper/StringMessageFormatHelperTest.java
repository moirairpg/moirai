package es.thalesalv.chatrpg.application.helper;

import java.util.List;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import es.thalesalv.chatrpg.application.util.StringProcessor;
import es.thalesalv.chatrpg.domain.model.EventData;
import es.thalesalv.chatrpg.testutils.EventDataUtils;
import es.thalesalv.chatrpg.testutils.TextMessageUtils;

@ExtendWith(MockitoExtension.class)
public class StringMessageFormatHelperTest {

    @InjectMocks
    private StringMessageFormatHelper stringMessageFormatHelper;

    @Test
    public void formatMessagesTest() {

        final List<String> messages = TextMessageUtils.createChat();
        final EventData eventData = EventDataUtils.buildEventData();
        final StringProcessor stringProcessor = new StringProcessor();
        stringProcessor.addRule(s -> Pattern.compile("\\{0\\}")
                .matcher(s)
                .replaceAll(r -> eventData.getChannelDefinitions()
                        .getChannelConfig()
                        .getPersona()
                        .getName()));

        final List<String> chatMessages = stringMessageFormatHelper.formatMessages(messages, eventData,
                stringProcessor);

        Assertions.assertEquals("This is a test persona", chatMessages.get(0));
        Assertions.assertEquals("John said: Hello", chatMessages.get(1));
        Assertions.assertEquals("Martha said: Hello, how are you?", chatMessages.get(2));
        Assertions.assertEquals("this is a bump", chatMessages.get(3));
        Assertions.assertEquals("John said: I am fine. How is the bot?", chatMessages.get(4));

        Assertions.assertEquals(
                "ChatRPG said: As an AI language model, I cannot be well or bad. But thank you for asking.",
                chatMessages.get(5));

        Assertions.assertEquals("Martha says: yeah right", chatMessages.get(6));
        Assertions.assertEquals("My name is ChatRPG and this is a nudge", chatMessages.get(7));
    }
}
