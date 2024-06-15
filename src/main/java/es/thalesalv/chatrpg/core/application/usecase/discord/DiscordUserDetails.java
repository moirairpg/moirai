package es.thalesalv.chatrpg.core.application.usecase.discord;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder(builderClassName = "Builder")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DiscordUserDetails {

    private final String id;
    private final String username;
    private final String globalName;
    private final String displayName;
    private final String mention;
}
