package es.thalesalv.gptbot.usecases;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

@Component
@RequiredArgsConstructor
public class ReplyQuoteUseCase {

    public void generateResponse(List<String> messages, User author, Message message, Message replyMessage) {

        messages.add(replyMessage.getAuthor().getAsTag() + " said earlier: " + replyMessage.getContentDisplay());
        messages.add(author.getAsTag()+ " replied to that message of " 
                + replyMessage.getAuthor().getAsTag() + "'s with: "+ message.getContentDisplay());
    }
}
