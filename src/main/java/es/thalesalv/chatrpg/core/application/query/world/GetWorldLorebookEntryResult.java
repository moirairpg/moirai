package es.thalesalv.chatrpg.core.application.query.world;

import java.time.OffsetDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(builderClassName = "Builder")
public final class GetWorldLorebookEntryResult {

    private final String id;
    private final String name;
    private final String regex;
    private final String description;
    private final String playerDiscordId;
    private final boolean isPlayerCharacter;
    private final OffsetDateTime creationDate;
    private final OffsetDateTime lastUpdateDate;
}