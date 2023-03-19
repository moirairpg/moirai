package es.thalesalv.chatrpg.domain.model.openai.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageEventData {

    private Guild guild;
    private SelfUser bot;
    private User messageAuthor;
    private Message responseMessage;
    private Message message;
    private MessageChannelUnion channel;
    private ChannelConfig channelConfig;
}
