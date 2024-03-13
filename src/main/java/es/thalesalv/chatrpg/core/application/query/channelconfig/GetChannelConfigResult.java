package es.thalesalv.chatrpg.core.application.query.channelconfig;

import java.time.OffsetDateTime;
import java.util.List;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder(builderClassName = "Builder")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class GetChannelConfigResult {

    private final String id;
    private final String name;
    private final String description;
    private final String personaId;
    private final String worldId;
    private final String visibility;
    private final String ownerDiscordId;
    private final List<String> writerUsers;
    private final List<String> readerUsers;
    private final OffsetDateTime creationDate;
    private final OffsetDateTime lastUpdateDate;
}
