package es.thalesalv.gptbot.usecases;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ReplyQuoteUseCase {

    public void generateResponse(List<String> messages, User author, Message message, Message replyMessage) {

        messages.add(replyMessage.getAuthor().getAsTag() + " said earlier: " + replyMessage.getContentDisplay());
        messages.add(author.getAsTag() + " replied to that message of " + replyMessage.getAuthor().getAsTag() + "'s with: " + message.getContentDisplay());
    }
}
