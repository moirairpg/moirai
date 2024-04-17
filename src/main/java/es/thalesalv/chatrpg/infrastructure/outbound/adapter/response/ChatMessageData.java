package es.thalesalv.chatrpg.infrastructure.outbound.adapter.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder(builderClassName = "Builder")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ChatMessageData {

    private final String id;
    private final String authorId;
    private final String channelId;
    private final String authorNickname;
    private final String authorUsername;
    private final String content;
}
