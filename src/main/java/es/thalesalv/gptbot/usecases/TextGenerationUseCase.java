package es.thalesalv.gptbot.usecases;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TextGenerationUseCase {

    public void generateResponse(List<String> messages, MessageChannelUnion channel) {
        channel.getHistory()
                .retrievePast(5)
                .complete()
                .forEach(m -> messages.add(m.getAuthor().getAsTag() + " said: " + m.getContentDisplay().trim()));

        Collections.reverse(messages);
    }
}
