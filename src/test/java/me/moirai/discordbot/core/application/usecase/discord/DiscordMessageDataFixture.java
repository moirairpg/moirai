package me.moirai.discordbot.core.application.usecase.discord;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DiscordMessageDataFixture {

    public static DiscordMessageData.Builder messageData() {

        return DiscordMessageData.builder()
                .id("2")
                .channelId("12345")
                .content("Message 2")
                .author(DiscordUserDetailsFixture.create().build());
    }

    public static List<DiscordMessageData> messageList(int amountOfMessages) {

        return IntStream.range(0, amountOfMessages)
                .mapToObj(index -> messageData().build())
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
