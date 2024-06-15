package es.thalesalv.chatrpg.core.application.usecase.persona.result;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class CreatePersonaResult {

    private final String id;

    public static CreatePersonaResult build(String id) {

        return new CreatePersonaResult(id);
    }
}
