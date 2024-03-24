package es.thalesalv.chatrpg.core.application.query.channelconfig;

import es.thalesalv.chatrpg.common.usecases.UseCase;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder(builderClassName = "Builder")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SearchChannelConfigsWithWriteAccess extends UseCase<SearchChannelConfigsResult> {

    private final Integer page;
    private final Integer items;
    private final String sortByField;
    private final String direction;
    private final String aiModel;
    private final String moderation;
    private final String name;
    private final String requesterDiscordId;
}
