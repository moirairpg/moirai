package es.thalesalv.chatrpg.core.application.query.world;

import es.thalesalv.chatrpg.common.usecases.UseCase;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder(builderClassName = "Builder")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SearchWorldsWithWriteAccess extends UseCase<SearchWorldsResult> {

    private final Integer page;
    private final Integer items;
    private final String sortByField;
    private final String direction;
    private final String name;
}
