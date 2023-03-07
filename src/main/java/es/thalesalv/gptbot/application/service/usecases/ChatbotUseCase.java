package es.thalesalv.gptbot.application.service.usecases;

import es.thalesalv.gptbot.application.config.MessageEventData;
import es.thalesalv.gptbot.application.config.Persona;
import es.thalesalv.gptbot.application.service.ModerationService;
import es.thalesalv.gptbot.application.service.interfaces.GptModelService;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ChatbotUseCase implements BotUseCase {

    private final ModerationService moderationService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatbotUseCase.class);

    private static String formatMessage(Message message, String botName) {
        return MessageFormat.format("{0} said: {1}", message.getAuthor().getName(),
                message.getContentDisplay().replaceAll("(@|)" + botName, StringUtils.EMPTY).trim());
    }

    private static String formatLastMessage(Message message, String botName) {
        Message referenceMessage = message.getReferencedMessage();
        return null == referenceMessage ?
                formatMessage(message, botName) :
                MessageFormat.format("{0} said earlier: {1}\n{2} quoted the message from {3} and replied with: {4}",
                        referenceMessage.getAuthor().getName(),
                        referenceMessage.getContentDisplay(),
                        message.getAuthor().getName(),
                        referenceMessage.getAuthor().getName(),
                        message.getContentDisplay());
    }

    @Override
    public void generateResponse(final Persona persona, final MessageEventData messageEventData, final Mentions mentions, final GptModelService model) {

        LOGGER.debug("Entered generation for normal text.");
        messageEventData.getChannel().sendTyping().complete();
        Message message = messageEventData.getMessage();
        Message referenceMessage = message.getReferencedMessage();
        MessageChannelUnion channel = messageEventData.getChannel();
        int limit = persona.getChatHistoryMemory();
        String botName = messageEventData.getBot().getName();
        List<String> messages = new ArrayList<>();
        messages.add(formatLastMessage(message, botName));
        Optional.ofNullable(referenceMessage)
                .map(r -> channel.getHistoryBefore(r, limit).complete().getRetrievedHistory())
                .orElseGet(() -> channel.getHistory().retrievePast(limit).complete())
                .stream()
                .map(m -> formatMessage(m, botName))
                .forEach(messages::add);
        Collections.reverse(messages);

        final String chatifiedMessage = chatifyMessages(messageEventData.getBot(), messages);

        moderationService.moderate(messageEventData, persona, chatifiedMessage)
                .subscribe(moderationResult -> model.generate(messageEventData, chatifiedMessage, persona, messages)
                        .subscribe(textResponse -> messageEventData.getChannel().sendMessage(textResponse).queue()));
    }


    private static String chatifyMessages(final User bot, final List<String> messages) {

        return MessageFormat.format("{0}\n{1} said: ",
                messages.stream().collect(Collectors.joining("\n")), bot.getName()).trim();
    }
}
