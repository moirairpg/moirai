package es.thalesalv.chatrpg.core.application.command.persona;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class CreatePersonaResult {

    private final String id;

    public static CreatePersonaResult with(String id) {

        return new CreatePersonaResult(id);
    }
}
