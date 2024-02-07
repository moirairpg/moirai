package es.thalesalv.chatrpg.core.domain;

import java.time.OffsetDateTime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Asset {

    private String creatorDiscordId;
    private OffsetDateTime creationDate;
    private OffsetDateTime lastUpdateDate;
}
