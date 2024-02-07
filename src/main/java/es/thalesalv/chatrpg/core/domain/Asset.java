package es.thalesalv.chatrpg.core.domain;

import java.time.OffsetDateTime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Asset {

    private final String creatorDiscordId;
    private final OffsetDateTime creationDate;
    private final OffsetDateTime lastUpdateDate;
}
