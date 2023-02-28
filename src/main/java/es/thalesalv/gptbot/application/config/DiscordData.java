package es.thalesalv.gptbot.application.config;

import lombok.Data;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.ChannelUnion;

@Data
public class DiscordData {

    private SelfUser bot;
    private User messageAuthor;
    private Guild guild;
    private ChannelUnion channel;
}
