package me.moirai.discordbot.core.application.usecase.discord.messagereceived;

import java.util.Collections;

public class MessageReceivedFixture {

    public static MessageReceived.Builder create() {

        return MessageReceived.builder()
                .authordDiscordId("John")
                .botUsername("TestBot")
                .botNickname("BotNickname")
                .isBotMentioned(false)
                .mentionedUsersIds(Collections.emptyList())
                .channelId("CHNLID")
                .guildId("GLDID")
                .messageId("MSGID");
    }
}
