package es.thalesalv.gptbot.usecases;

import java.text.MessageFormat;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

@Component
@RequiredArgsConstructor
public class ReplyQuoteUseCase {

    public void generateResponse(List<String> messages, User author, Message message, Message replyMessage) {

        
        messages.add(MessageFormat.format("{0} said earlier: {1}",
                replyMessage.getAuthor().getAsTag(), replyMessage.getContentDisplay()));

        messages.add(MessageFormat.format("{0} replied to that message from {1} with: {2}",
                author.getAsTag(), replyMessage.getAuthor().getAsTag(), replyMessage.getContentDisplay()));
    }
}
