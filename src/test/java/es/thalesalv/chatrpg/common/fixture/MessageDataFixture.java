package es.thalesalv.chatrpg.common.fixture;

import static es.thalesalv.chatrpg.common.fixture.UserDataFixture.humanUser;

import discord4j.discordjson.json.ImmutableMessageData;
import discord4j.discordjson.json.MessageData;

public class MessageDataFixture {

    public static ImmutableMessageData.Builder messageData() {

        return MessageData.builder()
                .id(2)
                .author(humanUser().build())
                .timestamp("64732647326432")
                .mentionEveryone(false)
                .tts(false)
                .pinned(false)
                .type(1)
                .content("Message 2")
                .channelId(432647326472L);
    }
}
