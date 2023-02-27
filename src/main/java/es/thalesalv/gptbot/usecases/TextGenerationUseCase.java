package es.thalesalv.gptbot.usecases;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import es.thalesalv.gptbot.data.ContextDatastore;
import es.thalesalv.gptbot.service.GptService;
import es.thalesalv.gptbot.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

@Component
@RequiredArgsConstructor
public class TextGenerationUseCase {

    private final GptService gptService;
    private final ContextDatastore contextDatastore;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RPGUseCase.class);

    public void generateResponse(SelfUser bot, Message message, MessageChannelUnion channel) {

        channel.sendTyping().complete();
        LOGGER.debug("Entered generation for normal text.");
        var messages = new ArrayList<String>();
        channel.getHistory().retrievePast(contextDatastore.getCurrentChannel().getChatHistoryMemory())
            .complete().forEach(m -> {
                var mAuthorUser = m.getAuthor();
                messages.add(MessageFormat.format("{0} (tagkey: {1}) said: {2}",
                    mAuthorUser.getName(), mAuthorUser.getAsMention(),
                    m.getContentDisplay().replaceAll("(@|)" + bot.getName(), StringUtils.EMPTY).trim()));
            });

        Collections.reverse(messages);
        MessageUtils.formatPersonality(messages, contextDatastore.getCurrentChannel(), bot);
        gptService.callDaVinci(MessageUtils.chatifyMessages(bot, messages))
            .filter(r -> !r.getChoices().get(0).getText().isBlank())
            .map(response -> {
                var responseText = response.getChoices().get(0).getText();
                message.getChannel().sendMessage(responseText.trim()).queue();
                return response;
            }).subscribe();
    }
}
