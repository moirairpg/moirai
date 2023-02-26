package es.thalesalv.gptbot.usecases;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

@Component
@RequiredArgsConstructor
public class TextGenerationUseCase {

    public void generateResponse(List<String> messages, MessageChannelUnion channel) {

        channel.getHistory()
                .retrievePast(5).complete()
                .forEach(m -> {
                    messages.add(MessageFormat.format("{0} said: {1}",
                        m.getAuthor().getAsTag(), m.getContentDisplay().trim()));
                });

        Collections.reverse(messages);
    }
}
