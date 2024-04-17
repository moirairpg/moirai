package es.thalesalv.chatrpg.core.application.query.discord.messagereceived;

import java.util.List;

import es.thalesalv.chatrpg.common.usecases.UseCase;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Getter
@Builder(builderClassName = "Builder")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MessageReceived extends UseCase<Mono<Void>> {

    private final String authordDiscordId;
    private final String messageId;
    private final String messageChannelId;
    private final String messageGuildId;
    private final String botName;
    private final List<String> mentionedUsersIds;
    private final boolean isBotMentioned;
}
