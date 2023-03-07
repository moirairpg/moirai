package es.thalesalv.gptbot.application.config;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

@Getter
@Setter
@Builder
public class MessageEventData {

    private Guild guild;
    private SelfUser bot;
    private User messageAuthor;
    private Message message;
    private MessageChannelUnion channel;
}
