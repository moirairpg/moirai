package es.thalesalv.gptbot.application.config;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MessageEventData {

    private String botId;
    private String messageAuthorId;
    private String messageId;
    private String channelId;
}
