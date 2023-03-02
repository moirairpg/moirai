package es.thalesalv.gptbot.application.translator;

import org.springframework.stereotype.Component;

import es.thalesalv.gptbot.application.config.MessageEventData;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

@Component
@RequiredArgsConstructor
public class MessageEventDataTranslator {

    public MessageEventData translate(final SelfUser bot, final User messageAuthor, final Message message, final MessageChannelUnion channel) {
        return MessageEventData.builder()
                .botId(bot.getId())
                .messageAuthorId(messageAuthor.getId())
                .messageId(message.getId())
                .channelId(channel.getId())
                .build();
    }
}
