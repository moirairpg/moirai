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
import es.thalesalv.gptbot.application.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

@Component
@RequiredArgsConstructor
public class ReplyQuoteUseCase {

    private final GptService gptService;
    private final ContextDatastore contextDatastore;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RPGUseCase.class);

    public void generateResponse(final SelfUser bot, final User author, final Message message, final Message replyMessage, final MessageChannelUnion channel) {

        channel.sendTyping().complete();
        LOGGER.debug("Entered generation for replies.");
        final List<String> messages = new ArrayList<>();
        replyMessage.getChannel()
            .getHistoryBefore(replyMessage, contextDatastore.getCurrentChannel().getChatHistoryMemory())
            .complete().getRetrievedHistory()
            .forEach(m -> {
                final User mAuthorUser = m.getAuthor();
                messages.add(MessageFormat.format("{0} (tagkey: {1}) said: {2}",
                        mAuthorUser.getName(), mAuthorUser.getAsMention(),
                        m.getContentDisplay().replaceAll("(@|)" + bot.getName(), StringUtils.EMPTY).trim()));
            });

        Collections.reverse(messages);
        messages.add(MessageFormat.format("{0} said earlier: {1}",
                replyMessage.getAuthor().getName(), replyMessage.getContentDisplay()));

        messages.add(MessageFormat.format("{0} quoted the message from {1} with: {2}",
                author.getName(), replyMessage.getAuthor().getName(), replyMessage.getContentDisplay()));

        MessageUtils.formatPersonality(messages, contextDatastore.getCurrentChannel(), bot);
        gptService.callDaVinci(MessageUtils.chatifyMessages(bot, messages))
            .filter(r -> !r.getChoices().get(0).getText().isBlank())
            .map(response -> {
                final String responseText = response.getChoices().get(0).getText();
                message.getChannel().sendMessage(responseText.trim()).queue();
                return response;
            }).subscribe();
    }
}
