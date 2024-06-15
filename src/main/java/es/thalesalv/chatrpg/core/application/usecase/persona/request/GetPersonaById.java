package es.thalesalv.chatrpg.core.application.usecase.persona.request;

import es.thalesalv.chatrpg.common.usecases.UseCase;
import es.thalesalv.chatrpg.core.application.usecase.persona.result.GetPersonaResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class GetPersonaById extends UseCase<GetPersonaResult> {

    private final String id;
    private final String requesterDiscordId;

    public static GetPersonaById build(String id, String requesterDiscordId) {

        return new GetPersonaById(id, requesterDiscordId);
    }
}
