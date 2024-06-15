package es.thalesalv.chatrpg.core.application.query.discord.messagereceived;

import java.util.Collections;

import es.thalesalv.chatrpg.core.application.usecase.discord.messagereceived.MessageReceived;

public class MessageReceivedFixture {

    public static MessageReceived.Builder create() {

        return MessageReceived.builder()
                .authordDiscordId("John")
                .botName("TestBot")
                .isBotMentioned(false)
                .mentionedUsersIds(Collections.emptyList())
                .messageChannelId("CHNLID")
                .messageGuildId("GLDID")
                .messageId("MSGID");
    }
}
