package es.thalesalv.chatrpg.application.service.usecases;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.application.config.MessageEventData;
import es.thalesalv.chatrpg.application.service.ModerationService;
import es.thalesalv.chatrpg.application.service.interfaces.GptModelService;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

@Component
@RequiredArgsConstructor
public class ChatbotUseCase implements BotUseCase {

    private final ModerationService moderationService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatbotUseCase.class);

    @Override
    public void generateResponse(final MessageEventData messageEventData, final Mentions mentions, final GptModelService model) {

        LOGGER.debug("Entered generation for normal text.");
        messageEventData.getChannel().sendTyping().complete();
        final List<String> messages = new ArrayList<>();
        final Message replyMessage = messageEventData.getMessage().getReferencedMessage();

        if (replyMessage != null) {
            handleMessageHistoryForReplies(messages, messageEventData);
        } else {
            handleMessageHistory(messages, messageEventData);
        }

        final String chatifiedMessage = chatifyMessages(messageEventData.getBot(), messages);
        moderationService.moderate(chatifiedMessage, messageEventData)
                .subscribe(moderationResult -> model.generate(chatifiedMessage, messages, messageEventData)
                .subscribe(textResponse -> messageEventData.getChannel().sendMessage(textResponse).queue()));
    }

    /**
     * Formats last messages history on replied to give the AI context on the past conversation
     * @param persona Persona containing the current bot settings
     * @param messages List of messages before the one replied to
     * @param message Message sent as a reply to the quoted message
     * @param bot Bot user
     * @param messageAuthor User who wrote the reply
     * @param channel Channel where the conversation is happening
     */
    private void handleMessageHistoryForReplies(final List<String> messages, final MessageEventData messageEventData) {

        LOGGER.debug("Entered quoted message history handling for chatbot");
        final Message message = messageEventData.getMessage();
        final Message reply = message.getReferencedMessage();
        messageEventData.getChannel().getHistoryBefore(reply, messageEventData.getPersona().getChatHistoryMemory())
                .complete()
                .getRetrievedHistory()
                .forEach(m -> {
                    final User mAuthorUser = m.getAuthor();
                    messages.add(MessageFormat.format("{0} said: {1}", mAuthorUser.getName(),
                            m.getContentDisplay().replaceAll("(@|)" + messageEventData.getBot().getName(), StringUtils.EMPTY).trim()));
                });

        Collections.reverse(messages);
        messages.add(MessageFormat.format("{0} said earlier: {1}",
                reply.getAuthor().getName(), reply.getContentDisplay()));

        messages.add(MessageFormat.format("{0} quoted the message from {1} and replied with: {2}",
                messageEventData.getMessageAuthor().getName(), reply.getAuthor().getName(), message.getContentDisplay()));
    }

    /**
     * Formats last messages history to give the AI context on the current conversation
     * @param persona Persona with the current bot settings
     * @param messages List messages before the one sent
     * @param channel Channel where the conversation is happening
     */
    private void handleMessageHistory(final List<String> messages, final MessageEventData messageEventData) {

        LOGGER.debug("Entered message history handling for chatbot");
        messageEventData.getChannel().getHistory()
                .retrievePast(messageEventData.getPersona().getChatHistoryMemory())
                .complete().forEach(m -> {
                    final User mAuthorUser = m.getAuthor();
                    messages.add(MessageFormat.format("{0} said: {1}", mAuthorUser.getName(),
                            m.getContentDisplay().trim()));
                });

        Collections.reverse(messages);
    }

    private static String chatifyMessages(final User bot, final List<String> messages) {

        return MessageFormat.format("{0}\n{1} said: ",
                String.join("\n", messages), bot.getName()).trim();
    }
}
