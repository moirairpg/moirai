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
import net.dv8tion.jda.api.entities.User;

@Component
@RequiredArgsConstructor
public class ReplyQuoteUseCase {

    private final GptService gptService;
    private final ContextDatastore contextDatastore;

    public void generateResponse(SelfUser bot, User author, Message message, Message replyMessage) {

        var messages = new ArrayList<String>();
        replyMessage.getChannel()
                .getHistoryBefore(replyMessage, contextDatastore.getCurrentChannel().getChatHistoryMemory())
                .complete().getRetrievedHistory()
                .forEach(m -> {
                    messages.add(MessageFormat.format("{0} said: {1}",
                        m.getAuthor().getName(), m.getContentDisplay().trim()));
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
                    var responseText = response.getChoices().get(0).getText();
                    message.getChannel().sendMessage(responseText.trim()).queue();
                    return response;
                }).subscribe();
    }
}
