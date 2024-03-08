package es.thalesalv.chatrpg.core.application.query.persona;

import es.thalesalv.chatrpg.common.cqrs.query.Query;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class GetPersonaById extends Query<GetPersonaResult> {

    private final String id;

    public static GetPersonaById build(String id) {

        return new GetPersonaById(id);
    }
}
