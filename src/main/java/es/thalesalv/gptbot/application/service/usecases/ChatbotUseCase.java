package es.thalesalv.gptbot.application.service.usecases;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import es.thalesalv.gptbot.adapters.data.ContextDatastore;
import es.thalesalv.gptbot.application.service.GptService;
import es.thalesalv.gptbot.application.service.ModerationService;
import es.thalesalv.gptbot.application.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

@Component
@RequiredArgsConstructor
public class ChatbotUseCase implements BotUseCase {

    private final GptService gptService;
    private final ContextDatastore contextDatastore;
    private final ModerationService moderationService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatbotUseCase.class);

    @Override
    public void generateResponse(final SelfUser bot, final User messageAuthor, final Message message, final MessageChannelUnion channel, final Mentions mentions) {

        LOGGER.debug("Entered generation for normal text.");
        channel.sendTyping().complete();
        final List<String> messages = new ArrayList<>();
        final Message replyMessage = message.getReferencedMessage();

        if (replyMessage != null) {
            formatContextForQuotedMessage(messages, message, replyMessage, bot, messageAuthor, channel);
        } else {
            formatContext(messages, bot, channel);
        }

        MessageUtils.formatPersonality(messages, contextDatastore.getPersona(), bot);
        final String chatifiedMessage = MessageUtils.chatifyMessages(bot, messages);
        moderationService.moderate(chatifiedMessage).map(moderationResult -> {
                gptService.callDaVinci(chatifiedMessage).map(textResponse -> {
                    channel.sendMessage(textResponse).queue();
                    return textResponse;
                }).subscribe();

            return moderationResult;
        }).subscribe();
    }

    private void formatContextForQuotedMessage(final List<String> messages, final Message message, final Message replyMessage, final SelfUser bot, final User messageAuthor, final MessageChannelUnion channel) {

        channel.getHistoryBefore(replyMessage, contextDatastore.getPersona().getChatHistoryMemory())
                .complete()
                .getRetrievedHistory()
                .forEach(m -> {
                    final User mAuthorUser = m.getAuthor();
                    messages.add(MessageFormat.format("{0} said: {2}",
                            mAuthorUser.getName(), mAuthorUser.getAsMention(),
                            m.getContentDisplay().replaceAll("(@|)" + bot.getName(), StringUtils.EMPTY).trim()));
                });

        Collections.reverse(messages);
        messages.add(MessageFormat.format("{0} said earlier: {1}",
                replyMessage.getAuthor().getName(), replyMessage.getContentDisplay()));

        messages.add(MessageFormat.format("{0} quoted the message from {1} and replied with: {2}",
                messageAuthor.getName(), replyMessage.getAuthor().getName(), message.getContentDisplay()));
    }

    private void formatContext(final List<String> messages, final SelfUser bot, final MessageChannelUnion channel) {

        channel.getHistory()
                .retrievePast(contextDatastore.getPersona().getChatHistoryMemory())
                .complete().forEach(m -> {
                    final User mAuthorUser = m.getAuthor();
                    messages.add(MessageFormat.format("{0} said: {1}", mAuthorUser.getName(),
                            m.getContentDisplay().replaceAll("(@|)" + bot.getName(), StringUtils.EMPTY).trim()));
                });;

        Collections.reverse(messages);
    }
}
