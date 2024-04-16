package es.thalesalv.chatrpg.core.application.model.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ChatMessage {

    public enum Role {

        SYSTEM,
        ASSISTANT,
        USER;
    }

    private final Role role;
    private final String content;

    public static ChatMessage build(Role role, String content) {

        return new ChatMessage(role, content);
    }
}
