package es.thalesalv.gptbot.application.service.usecases;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import es.thalesalv.gptbot.application.config.MessageEventData;
import es.thalesalv.gptbot.application.config.Persona;
import es.thalesalv.gptbot.application.service.ModerationService;
import es.thalesalv.gptbot.application.service.interfaces.GptModelService;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

@Component
@RequiredArgsConstructor
public class ChatbotUseCase implements BotUseCase {

    private final ModerationService moderationService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatbotUseCase.class);

    @Override
    public void generateResponse(final Persona persona, final MessageEventData messageEventData, final Mentions mentions, final GptModelService model) {

        LOGGER.debug("Entered generation for normal text.");
        messageEventData.getChannel().sendTyping().complete();
        final List<String> messages = new ArrayList<>();
        final Message replyMessage = messageEventData.getMessage().getReferencedMessage();

        if (replyMessage != null) {
            handleMessageHistoryForReplies(persona, messages, messageEventData.getMessage(),
                    messageEventData.getBot(), messageEventData.getMessageAuthor(), messageEventData.getChannel());
        } else {
            handleMessageHistory(persona, messages, messageEventData.getBot(), messageEventData.getChannel());
        }

        final String chatifiedMessage = chatifyMessages(messageEventData.getBot(), messages);
        moderationService.moderate(messageEventData, persona, chatifiedMessage)
                .subscribe(moderationResult -> model.generate(messageEventData, chatifiedMessage, persona, messages)
                .subscribe(textResponse -> messageEventData.getChannel().sendMessage(textResponse).queue()));
    }

    /**
     * Formats last messages history on replied to give the AI context on the past conversation
     * @param persona Persona containing the current bot settings
     * @param messages List of messages before the one replied to
     * @param message Message sent as a reply to the quoted message
     * @param replyMessage Quoted message
     * @param bot Bot user
     * @param messageAuthor User who wrote the reply
     * @param channel Channel where the conversation is happening
     */
    private void handleMessageHistoryForReplies(final Persona persona, final List<String> messages,
            final Message message, final SelfUser bot, final User messageAuthor, final MessageChannelUnion channel) {

        LOGGER.debug("Entered quoted message history handling for chatbot");
        Message reply = message.getReferencedMessage();
        channel.getHistoryBefore(reply, persona.getChatHistoryMemory())
                .complete()
                .getRetrievedHistory()
                .forEach(m -> {
                    final User mAuthorUser = m.getAuthor();
                    messages.add(MessageFormat.format("{0} said: {1}", mAuthorUser.getName(),
                            m.getContentDisplay().replaceAll("(@|)" + bot.getName(), StringUtils.EMPTY).trim()));
                });

        Collections.reverse(messages);
        messages.add(MessageFormat.format("{0} said earlier: {1}",
                reply.getAuthor().getName(), reply.getContentDisplay()));

        messages.add(MessageFormat.format("{0} quoted the message from {1} and replied with: {2}",
                messageAuthor.getName(), reply.getAuthor().getName(), message.getContentDisplay()));
    }

    /**
     * Formats last messages history to give the AI context on the current conversation
     * @param persona Persona with the current bot settings
     * @param messages List messages before the one sent
     * @param bot Bot user
     * @param channel Channel where the conversation is happening
     */
    private void handleMessageHistory(final Persona persona, final List<String> messages, final SelfUser bot, final MessageChannelUnion channel) {

        LOGGER.debug("Entered message history handling for chatbot");
        channel.getHistory()
                .retrievePast(persona.getChatHistoryMemory())
                .complete().forEach(m -> {
                    final User mAuthorUser = m.getAuthor();
                    messages.add(MessageFormat.format("{0} said: {1}", mAuthorUser.getName(),
                            m.getContentDisplay().replaceAll("(@|)" + bot.getName(), StringUtils.EMPTY).trim()));
                });;

        Collections.reverse(messages);
    }

    private static String chatifyMessages(final User bot, final List<String> messages) {

        return MessageFormat.format("{0}\n{1} said: ",
                messages.stream().collect(Collectors.joining("\n")), bot.getName()).trim();
    }
}
