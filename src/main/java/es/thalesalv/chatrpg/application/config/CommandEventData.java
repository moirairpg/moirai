package es.thalesalv.chatrpg.application.config;

import es.thalesalv.chatrpg.adapters.data.db.entity.ChannelConfig;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

@Getter
@Setter
@Builder
public class CommandEventData {

    private Message messageToBeEdited;
    private String lorebookEntryId;
    private String lorebookEntryRegexId;
    private SelfUser discordUser;
    private ChannelConfig channelConfig;
    private MessageChannelUnion channel;
}
