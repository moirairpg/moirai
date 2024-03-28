package es.thalesalv.chatrpg.core.application.query.world;

import es.thalesalv.chatrpg.common.usecases.UseCase;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder(builderClassName = "Builder")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class GetWorldLorebookEntryById extends UseCase<GetWorldLorebookEntryResult> {

    private final String entryId;
    private final String worldId;
    private final String requesterDiscordId;
}
