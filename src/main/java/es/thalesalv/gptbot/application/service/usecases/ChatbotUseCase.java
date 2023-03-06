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

import es.thalesalv.gptbot.adapters.data.ContextDatastore;
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

    private final ContextDatastore contextDatastore;
    private final ModerationService moderationService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatbotUseCase.class);

    @Override
    public void generateResponse(final SelfUser bot, final User messageAuthor, final MessageEventData messageEventData, final MessageChannelUnion channel, final Mentions mentions, final GptModelService model) {

        LOGGER.debug("Entered generation for normal text.");
        channel.sendTyping().complete();
        final List<String> messages = new ArrayList<>();
        final Message replyMessage = messageEventData.getMessage().getReferencedMessage();

        if (replyMessage != null) {
            handleMessageHistoryForReplies(messages, messageEventData.getMessage(), bot, messageAuthor, channel);
        } else {
            handleMessageHistory(messages, bot, channel);
        }

        final String chatifiedMessage = chatifyMessages(bot, messages);
        final Persona persona = contextDatastore.getPersona();
        moderationService.moderate(chatifiedMessage)
                .subscribe(moderationResult -> model.generate(messageEventData, chatifiedMessage, persona, messages)
                .subscribe(textResponse -> channel.sendMessage(textResponse).queue()));
    }

    /**
     * Formats last messages history on replied to give the AI context on the past conversation
     * @param messages List of messages before the one replied to
     * @param message Message sent as a reply to the quoted message
     * @param replyMessage Quoted message
     * @param bot Bot user
     * @param messageAuthor User who wrote the reply
     * @param channel Channel where the conversation is happening
     */
    private void handleMessageHistoryForReplies(final List<String> messages, final Message message, final SelfUser bot, final User messageAuthor, final MessageChannelUnion channel) {

        LOGGER.debug("Entered quoted message history handling for chatbot");
        Message reply = message.getReferencedMessage();
        channel.getHistoryBefore(reply, contextDatastore.getPersona().getChatHistoryMemory())
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
     * @param messages List messages before the one sent
     * @param bot Bot user
     * @param channel Channel where the conversation is happening
     */
    private void handleMessageHistory(final List<String> messages, final SelfUser bot, final MessageChannelUnion channel) {

        LOGGER.debug("Entered message history handling for chatbot");
        channel.getHistory()
                .retrievePast(contextDatastore.getPersona().getChatHistoryMemory())
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
