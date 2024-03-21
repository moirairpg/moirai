package es.thalesalv.chatrpg.core.application.command.world;

import java.time.OffsetDateTime;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class UpdateWorldLorebookEntryResult {

    private final OffsetDateTime lastUpdatedDateTime;

    public static UpdateWorldLorebookEntryResult build(OffsetDateTime lastUpdatedDateTime) {

        return new UpdateWorldLorebookEntryResult(lastUpdatedDateTime);
    }
}
