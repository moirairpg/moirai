package es.thalesalv.chatrpg.core.application.command.world;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class CreateWorldResult {

    private final String id;

    public static CreateWorldResult with(String id) {

        return new CreateWorldResult(id);
    }
}
