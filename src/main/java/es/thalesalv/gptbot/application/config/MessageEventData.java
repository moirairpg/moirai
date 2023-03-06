package es.thalesalv.gptbot.application.config;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Message;

@Getter
@Setter
@Builder
public class MessageEventData {

    private String botId;
    private String messageAuthorId;
    private Message message;
    private String channelId;
}
