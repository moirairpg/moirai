package es.thalesalv.chatrpg.infrastructure.outbound.adapter.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatMessage {

    private final String role;
    private final String content;

    public static ChatMessage build(String role, String content) {

        return new ChatMessage(role, content);
    }
}
