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
public class ChatbotUseCase implements BotUseCase {

    private final GptService gptService;
    private final ContextDatastore contextDatastore;
    private final ModerationService moderationService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatbotUseCase.class);

    @Override
    public void generateResponse(final SelfUser bot, final User messageAuthor, final Message message, final MessageChannelUnion channel, final Mentions mentions) {

        channel.sendTyping().queue(a -> {
            LOGGER.debug("Entered generation for normal text.");
            final List<String> messages = new ArrayList<>();
            channel.getHistory()
                    .retrievePast(contextDatastore.getCurrentChannel().getChatHistoryMemory())
                    .queue(ms -> {
                        ms.forEach(m -> {
                            final User mAuthorUser = m.getAuthor();
                            messages.add(MessageFormat.format("{0} said: {1}", mAuthorUser.getName(),
                                    m.getContentDisplay().replaceAll("(@|)" + bot.getName(), StringUtils.EMPTY).trim()));
                        });
                    });
    
            Collections.reverse(messages);
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
