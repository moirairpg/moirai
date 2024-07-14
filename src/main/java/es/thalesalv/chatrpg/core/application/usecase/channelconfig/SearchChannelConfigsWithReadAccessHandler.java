package es.thalesalv.chatrpg.core.application.usecase.channelconfig;

import es.thalesalv.chatrpg.common.annotation.UseCaseHandler;
import es.thalesalv.chatrpg.common.usecases.AbstractUseCaseHandler;
import es.thalesalv.chatrpg.core.application.usecase.channelconfig.request.SearchChannelConfigsWithReadAccess;
import es.thalesalv.chatrpg.core.application.usecase.channelconfig.result.SearchChannelConfigsResult;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfigRepository;

@UseCaseHandler
public class SearchChannelConfigsWithReadAccessHandler extends AbstractUseCaseHandler<SearchChannelConfigsWithReadAccess, SearchChannelConfigsResult> {

    private final ChannelConfigRepository repository;

    public SearchChannelConfigsWithReadAccessHandler(ChannelConfigRepository repository) {
        this.repository = repository;
    }

    @Override
    public SearchChannelConfigsResult execute(SearchChannelConfigsWithReadAccess query) {

        return repository.searchChannelConfigsWithReadAccess(query);
    }
}
