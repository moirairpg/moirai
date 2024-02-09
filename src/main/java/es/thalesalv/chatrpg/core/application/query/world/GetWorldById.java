package es.thalesalv.chatrpg.core.application.query.world;

import es.thalesalv.chatrpg.common.cqrs.query.Query;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GetWorldById extends Query<GetWorldByIdResult> {

    private final String id;

    public static GetWorldById with(String id) {

        return new GetWorldById(id);
    }
}
