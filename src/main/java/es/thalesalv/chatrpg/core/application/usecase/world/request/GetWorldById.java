package es.thalesalv.chatrpg.core.application.usecase.world.request;

import es.thalesalv.chatrpg.common.usecases.UseCase;
import es.thalesalv.chatrpg.core.application.usecase.world.result.GetWorldResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class GetWorldById extends UseCase<GetWorldResult> {

    private final String id;
    private final String requesterDiscordId;

    public static GetWorldById build(String id, String requesterDiscordId) {

        return new GetWorldById(id, requesterDiscordId);
    }
}
