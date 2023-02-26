package es.thalesalv.gptbot.usecases;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;

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

    public void generateResponse(SelfUser bot, Message message, MessageChannelUnion channel) {

        var messages = new ArrayList<String>();
        channel.getHistory()
                .retrievePast(5).complete()
                .forEach(m -> {
                    messages.add(MessageFormat.format("{0} said: {1}",
                        m.getAuthor().getName(), m.getContentDisplay().trim()));
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
