package es.thalesalv.chatrpg.core.application.usecase.channelconfig.request;

import es.thalesalv.chatrpg.common.usecases.UseCase;
import es.thalesalv.chatrpg.core.application.usecase.channelconfig.result.SearchChannelConfigsResult;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder(builderClassName = "Builder")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SearchChannelConfigsWithReadAccess extends UseCase<SearchChannelConfigsResult> {

    private final Integer page;
    private final Integer items;
    private final String sortByField;
    private final String direction;
    private final String aiModel;
    private final String moderation;
    private final String name;
    private final String requesterDiscordId;
}
