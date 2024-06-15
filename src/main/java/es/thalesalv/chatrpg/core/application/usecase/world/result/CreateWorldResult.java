package es.thalesalv.chatrpg.core.application.usecase.world.result;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class CreateWorldResult {

    private final String id;

    public static CreateWorldResult build(String id) {

        return new CreateWorldResult(id);
    }
}
