package es.thalesalv.gptbot.usecases;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import es.thalesalv.gptbot.service.GptService;
import es.thalesalv.gptbot.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

@Component
@RequiredArgsConstructor
public class BotMentionedUseCase {

    private final GptService gptService;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RPGUseCase.class);
    
    public void generateResponse(Message message, MessageChannelUnion channel, SelfUser bot) {

        channel.sendTyping().complete();
        LOGGER.debug("Entered genration for mentions");
        var messages = new ArrayList<String>();
        channel.getHistory().retrievePast(10)
            .complete().forEach(m -> {
                var mAuthorUser = m.getAuthor();
                messages.add(MessageFormat.format("{0} (tagkey: {1}) said: {2}",
                    mAuthorUser.getName(), mAuthorUser.getAsMention(),
                    m.getContentDisplay().replaceAll("(@|)" + bot.getName(), StringUtils.EMPTY).trim()));
            });

        Collections.reverse(messages);

        var botInstructions = MessageFormat.format("I am {0}, and my nickname is {1}. I am a GPT-3 chatbot that is part of this chat on Discord. "
                + "I speak only in the language of the internet. "
                + "Although I'm a polite bot, I won't take things very seriously because I'm a prankster and very playful.",
                bot.getAsTag(), bot.getName());

        messages.add(botInstructions);
        gptService.callDaVinci(MessageUtils.chatifyMessages(bot, messages))
            .filter(r -> !r.getChoices().get(0).getText().isBlank())
            .map(response -> {
                var responseText = response.getChoices().get(0).getText();
                message.getChannel().sendMessage(responseText.trim()).queue();
                return response;
            }).subscribe();
    }
}
