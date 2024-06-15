package es.thalesalv.chatrpg.core.application.usecase.persona.request;

import es.thalesalv.chatrpg.common.usecases.UseCase;
import es.thalesalv.chatrpg.core.application.usecase.persona.result.SearchPersonasResult;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder(builderClassName = "Builder")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SearchPersonasWithReadAccess extends UseCase<SearchPersonasResult> {

    private final Integer page;
    private final Integer items;
    private final String searchField;
    private final String searchCriteria;
    private final String sortByField;
    private final String direction;
    private final String name;
    private final String gameMode;
    private final String requesterDiscordId;
}
