package me.moirai.discordbot.infrastructure.outbound.adapter.response;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ChatMessageDataFixture {

    public static ChatMessageData.Builder messageData() {

        return ChatMessageData.builder()
                .id("2")
                .authorId("12345")
                .authorNickname("authorNickname")
                .authorUsername("authorUsername")
                .channelId("12345")
                .content("Message 2");
    }

    public static List<ChatMessageData> messageList(int amountOfMessages) {

        return IntStream.range(0, amountOfMessages)
                .mapToObj(index -> messageData().build())
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
