package es.thalesalv.chatrpg.core.application.command.persona;

import java.time.OffsetDateTime;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class UpdatePersonaResult {

    private final OffsetDateTime lastUpdatedDateTime;

    public static UpdatePersonaResult build(OffsetDateTime lastUpdatedDateTime) {

        return new UpdatePersonaResult(lastUpdatedDateTime);
    }
}
