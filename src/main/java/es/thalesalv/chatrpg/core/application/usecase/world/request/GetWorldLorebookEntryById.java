package es.thalesalv.chatrpg.core.application.usecase.world.request;

import es.thalesalv.chatrpg.common.usecases.UseCase;
import es.thalesalv.chatrpg.core.application.usecase.world.result.GetWorldLorebookEntryResult;
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
