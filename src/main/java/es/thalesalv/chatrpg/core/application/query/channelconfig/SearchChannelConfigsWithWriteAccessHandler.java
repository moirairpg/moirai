package es.thalesalv.chatrpg.core.application.query.channelconfig;

import es.thalesalv.chatrpg.common.annotation.UseCaseHandler;
import es.thalesalv.chatrpg.common.usecases.AbstractUseCaseHandler;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfigRepository;
import lombok.RequiredArgsConstructor;

@UseCaseHandler
@RequiredArgsConstructor
public class SearchChannelConfigsWithWriteAccessHandler extends AbstractUseCaseHandler<SearchChannelConfigsWithWriteAccess, SearchChannelConfigsResult> {

    private final ChannelConfigRepository repository;

    @Override
    public SearchChannelConfigsResult execute(SearchChannelConfigsWithWriteAccess query) {

        return repository.searchChannelConfigsWithWriteAccess(query);
    }
}
