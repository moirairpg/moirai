package es.thalesalv.chatrpg.domain.model.openai.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommandEventData {

    private Message messageToBeEdited;
    private String lorebookEntryId;
    private String lorebookEntryRegexId;
    private SelfUser discordUser;
    private ChannelConfig channelConfig;
    private MessageChannelUnion channel;
}
