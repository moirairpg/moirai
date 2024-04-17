package es.thalesalv.chatrpg.infrastructure.outbound.adapter.response;

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
}
