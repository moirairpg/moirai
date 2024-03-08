package es.thalesalv.chatrpg.core.application.query.world;

import es.thalesalv.chatrpg.common.usecases.UseCase;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder(builderClassName = "Builder")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SearchWorlds extends UseCase<SearchWorldsResult> {

    private final Integer page;
    private final Integer results;
    private final String searchField;
    private final String searchCriteria;
    private final String sortByField;
    private final String order;
}
