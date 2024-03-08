package es.thalesalv.chatrpg.core.application.query.world;

import es.thalesalv.chatrpg.common.cqrs.query.Query;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class GetWorldById extends Query<GetWorldResult> {

    private final String id;

    public static GetWorldById build(String id) {

        return new GetWorldById(id);
    }
}
