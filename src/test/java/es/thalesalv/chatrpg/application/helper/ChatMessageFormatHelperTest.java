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
import es.thalesalv.chatrpg.domain.model.openai.completion.ChatMessage;
import es.thalesalv.chatrpg.testutils.EventDataUtils;
import es.thalesalv.chatrpg.testutils.TextMessageUtils;

@ExtendWith(MockitoExtension.class)
public class ChatMessageFormatHelperTest {

    @InjectMocks
    private ChatMessageFormatHelper chatMessageFormatHelper;

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

        final List<ChatMessage> chatMessages = chatMessageFormatHelper.formatMessages(messages, eventData,
                stringProcessor);

        Assertions.assertEquals("system", chatMessages.get(0)
                .getRole());
        Assertions.assertEquals("This is a test persona", chatMessages.get(0)
                .getContent());

        Assertions.assertEquals("user", chatMessages.get(1)
                .getRole());
        Assertions.assertEquals("John said: Hello", chatMessages.get(1)
                .getContent());

        Assertions.assertEquals("user", chatMessages.get(2)
                .getRole());
        Assertions.assertEquals("Martha said: Hello, how are you?", chatMessages.get(2)
                .getContent());

        Assertions.assertEquals("system", chatMessages.get(3)
                .getRole());
        Assertions.assertEquals("this is a bump", chatMessages.get(3)
                .getContent());

        Assertions.assertEquals("user", chatMessages.get(4)
                .getRole());
        Assertions.assertEquals("John said: I am fine. How is the bot?", chatMessages.get(4)
                .getContent());

        Assertions.assertEquals("assistant", chatMessages.get(5)
                .getRole());
        Assertions.assertEquals("As an AI language model, I cannot be well or bad. But thank you for asking.",
                chatMessages.get(5)
                        .getContent());

        Assertions.assertEquals("user", chatMessages.get(6)
                .getRole());
        Assertions.assertEquals("Martha says: yeah right", chatMessages.get(6)
                .getContent());

        Assertions.assertEquals("system", chatMessages.get(7)
                .getRole());
        Assertions.assertEquals("My name is ChatRPG and this is a nudge", chatMessages.get(7)
                .getContent());
    }
}
