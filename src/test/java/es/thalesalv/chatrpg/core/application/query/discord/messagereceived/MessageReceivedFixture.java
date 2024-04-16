package es.thalesalv.chatrpg.core.application.query.discord.messagereceived;

import java.util.Collections;

public class MessageReceivedFixture {

    public static MessageReceived.Builder create() {

        return MessageReceived.builder()
                .authordDiscordId("John")
                .botName("TestBot")
                .isBotMentioned(false)
                .mentionedUsersIds(Collections.emptyList())
                .messageChannelId("CHNLID")
                .messageContent("Hello, world!")
                .messageGuildId("GLDID")
                .messageId("MSGID");
    }
}
