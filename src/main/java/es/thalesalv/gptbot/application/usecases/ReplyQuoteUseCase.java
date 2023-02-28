package es.thalesalv.gptbot.application.usecases;

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
public class ReplyQuoteUseCase implements BotUseCase {

    private final GptService gptService;
    private final ContextDatastore contextDatastore;
    private final ModerationService moderationService;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ReplyQuoteUseCase.class);
    private static final String MESSAGE_FLAGGED = "The message you sent has content that was flagged by OpenAI''s moderation. Message content: \n{0}";

    @Override
    public void generateResponse(SelfUser bot, User author, Message message, MessageChannelUnion channel, final Mentions mentions) {

        LOGGER.debug("Entered generation for replies.");
        channel.sendTyping().queue(a -> {
            final Message replyMessage = message.getReferencedMessage();
            final List<String> messages = new ArrayList<>();
            channel.getHistoryBefore(replyMessage, contextDatastore.getCurrentChannel().getChatHistoryMemory())
                .complete().getRetrievedHistory()
                .forEach(m -> {
                    final User mAuthorUser = m.getAuthor();
                    messages.add(MessageFormat.format("{0} said: {2}",
                            mAuthorUser.getName(), mAuthorUser.getAsMention(),
                            m.getContentDisplay().replaceAll("(@|)" + bot.getName(), StringUtils.EMPTY).trim()));
                });

            Collections.reverse(messages);
            messages.add(MessageFormat.format("{0} said earlier: {1}",
                    replyMessage.getAuthor().getName(), replyMessage.getContentDisplay()));

            messages.add(MessageFormat.format("{0} quoted the message from {1} with: {2}",
                    author.getName(), replyMessage.getAuthor().getName(), replyMessage.getContentDisplay()));

            MessageUtils.formatPersonality(messages, contextDatastore.getCurrentChannel(), bot);
            final String chatifiedMessage = MessageUtils.chatifyMessages(bot, messages);

            moderationService.moderate(chatifiedMessage).map(moderationResult -> {
                gptService.callDaVinci(chatifiedMessage).map(textResponse -> {
                    channel.sendMessage(textResponse).queue();
                    return textResponse;
                }).subscribe();

                return moderationResult;
            }).subscribe();
        });
    }
}
