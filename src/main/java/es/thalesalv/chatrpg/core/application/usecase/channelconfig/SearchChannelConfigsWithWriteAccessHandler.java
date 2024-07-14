package es.thalesalv.chatrpg.core.application.usecase.channelconfig;

import es.thalesalv.chatrpg.common.annotation.UseCaseHandler;
import es.thalesalv.chatrpg.common.usecases.AbstractUseCaseHandler;
import es.thalesalv.chatrpg.core.application.usecase.channelconfig.request.SearchChannelConfigsWithWriteAccess;
import es.thalesalv.chatrpg.core.application.usecase.channelconfig.result.SearchChannelConfigsResult;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfigRepository;

@UseCaseHandler
public class SearchChannelConfigsWithWriteAccessHandler extends AbstractUseCaseHandler<SearchChannelConfigsWithWriteAccess, SearchChannelConfigsResult> {

    private final ChannelConfigRepository repository;

    public SearchChannelConfigsWithWriteAccessHandler(ChannelConfigRepository repository) {
        this.repository = repository;
    }

    @Override
    public SearchChannelConfigsResult execute(SearchChannelConfigsWithWriteAccess query) {

        return repository.searchChannelConfigsWithWriteAccess(query);
    }
}
