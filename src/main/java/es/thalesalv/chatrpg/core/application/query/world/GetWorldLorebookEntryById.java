package es.thalesalv.chatrpg.core.application.query.world;

import es.thalesalv.chatrpg.common.usecases.UseCase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class GetWorldLorebookEntryById extends UseCase<GetWorldLorebookEntryResult> {

    private final String id;

    public static GetWorldLorebookEntryById build(String id) {

        return new GetWorldLorebookEntryById(id);
    }
}
