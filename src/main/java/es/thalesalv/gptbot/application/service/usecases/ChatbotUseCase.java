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
import java.util.Arrays;
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

    private static String formatMessageWithReply(Message message) {
        return MessageFormat.format("{0} said earlier: {1}\n{2} quoted the message from {3} and replied with: {4}",
                Optional.ofNullable(message.getReferencedMessage()).map(Message::getAuthor).map(User::getName).orElse(""),
                Optional.ofNullable(message.getReferencedMessage()).map(Message::getContentDisplay).orElse(""),
                message.getAuthor().getName(),
                Optional.ofNullable(message.getReferencedMessage()).map(Message::getAuthor).map(User::getName).orElse(""),
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
        final List<String> messages = Optional.ofNullable(referenceMessage)
                .map(r -> channel.getHistoryBefore(r, limit).complete().getRetrievedHistory())
                .orElseGet(() -> channel.getHistory().retrievePast(limit).complete())
                .stream().map(m ->
                        null == m.getReferencedMessage() ?
                                formatMessage(m, botName) :
                                formatMessageWithReply(m)
                )
                .collect(Collectors.toList());
        Collections.reverse(messages);

        if (null != referenceMessage) {
            messages.addAll(Arrays.asList(
                    MessageFormat.format("{0} said earlier: {1}",
                            referenceMessage.getAuthor().getName(), referenceMessage.getContentDisplay()),
                    MessageFormat.format("{0} quoted the message from {1} and replied with: {2}",
                            message.getAuthor().getName(), referenceMessage.getAuthor().getName(), message.getContentDisplay())
            ));
        }

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
