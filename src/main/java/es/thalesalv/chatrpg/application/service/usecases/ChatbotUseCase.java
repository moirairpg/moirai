package es.thalesalv.chatrpg.application.service.usecases;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.application.config.MessageEventData;
import es.thalesalv.chatrpg.application.config.Persona;
import es.thalesalv.chatrpg.application.service.ModerationService;
import es.thalesalv.chatrpg.application.service.interfaces.GptModelService;
import es.thalesalv.chatrpg.domain.exception.DiscordFunctionException;
import lombok.RequiredArgsConstructor;
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
    public MessageEventData generateResponse(final MessageEventData eventData, final GptModelService model) {

        LOGGER.debug("Entered generation for normal text.");
        eventData.getChannel().sendTyping().complete();
        final Message message = eventData.getMessage();
        final SelfUser bot = eventData.getBot();
        if (message.getContentRaw().trim().equals(bot.getAsMention().trim())) {
            message.delete().submit().whenComplete((d, e) -> {
                if (e != null) {
                    LOGGER.error("Error deleting trigger mention in RPG", e);
                    throw new DiscordFunctionException("Error deleting trigger mention in RPG", e);
                }
            });
        }

        final List<String> messages = new ArrayList<>();
        final Message replyMessage = message.getReferencedMessage();
        if (replyMessage != null) {
            handleMessageHistoryForReplies(messages, eventData);
        } else {
            handleMessageHistory(messages, eventData);
        }

        final String chatifiedMessage = chatifyMessages(messages, eventData);
        moderationService.moderate(chatifiedMessage, eventData)
                .subscribe(inputModeration -> model.generate(chatifiedMessage, messages, eventData)
                .subscribe(textResponse -> moderationService.moderateOutput(textResponse, eventData)
                .subscribe(outputModeration -> {
                    final Message responseMessage = eventData.getChannel().sendMessage(textResponse).complete();
                    eventData.setResponseMessage(responseMessage);
                })));

        return eventData;
    }

    /**
     * Formats last messages history on replied to give the AI context on the past conversation
     * @param messages List of messages before the one replied to
     * @param eventData Object containing the event's important data to be processed
     */
    private void handleMessageHistoryForReplies(final List<String> messages, final MessageEventData eventData) {

        LOGGER.debug("Entered quoted message history handling for chatbot");
        final Message message = eventData.getMessage();
        final SelfUser bot = eventData.getBot();
        final Message reply = message.getReferencedMessage();
        final MessageChannelUnion channel = eventData.getChannel();
        final Persona persona = eventData.getPersona();
        channel.getHistoryBefore(reply, persona.getChatHistoryMemory())
                .complete()
                .getRetrievedHistory()
                .stream()
                .filter(m -> !m.getContentRaw().trim().equals(bot.getAsMention().trim()))
                .forEach(m -> {
                    final User mAuthorUser = m.getAuthor();
                    messages.add(MessageFormat.format("{0} said: {1}", mAuthorUser.getName(),
                            m.getContentDisplay()).trim());
                });

        Collections.reverse(messages);
        messages.add(MessageFormat.format("{0} said earlier: {1}",
                reply.getAuthor().getName(), reply.getContentDisplay()));

        messages.add(MessageFormat.format("{0} quoted the message from {1} and replied with: {2}",
                eventData.getMessageAuthor().getName(), reply.getAuthor().getName(), message.getContentDisplay()));
    }

    /**
     * Formats last messages history to give the AI context on the current conversation
     * @param messages List messages before the one sent
     * @param eventData Object containing the event's important data to be processed
     */
    private void handleMessageHistory(final List<String> messages, final MessageEventData eventData) {

        LOGGER.debug("Entered message history handling for chatbot");
        final Persona persona = eventData.getPersona();
        final MessageChannelUnion channel = eventData.getChannel();
        final SelfUser bot = eventData.getBot();
        channel.getHistory()
                .retrievePast(persona.getChatHistoryMemory()).complete()
                .stream()
                .filter(m -> !m.getContentRaw().trim().equals(bot.getAsMention().trim()))
                .forEach(m -> {
                    final User mAuthorUser = m.getAuthor();
                    messages.add(MessageFormat.format("{0} said: {1}", mAuthorUser.getName(),
                            m.getContentDisplay().trim()));
                });

        Collections.reverse(messages);
    }

    /**
     * Stringifies messages and turns them into a prompt format
     * 
     * @param messages Messages in the chat room
     * @param eventData Object containing event data
     * @return Stringified messages for prompt
     */
    private static String chatifyMessages(final List<String> messages, final MessageEventData eventData) {

        LOGGER.debug("Entered chatbot conversation formatter");
        messages.replaceAll(m -> m.replace(eventData.getBot().getName(), eventData.getPersona().getName()));
        return MessageFormat.format("{0}\n{1} said: ",
                String.join("\n", messages), eventData.getPersona().getName()).trim();
    }
}
