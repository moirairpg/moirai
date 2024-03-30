package es.thalesalv.chatrpg.core.application.query.channelconfig;

import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.common.usecases.UseCaseHandler;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfigRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchChannelConfigsWithReadAccessHandler extends UseCaseHandler<SearchChannelConfigsWithReadAccess, SearchChannelConfigsResult> {

    private final ChannelConfigRepository repository;

    @Override
    public SearchChannelConfigsResult execute(SearchChannelConfigsWithReadAccess query) {

        return repository.searchChannelConfigsWithReadAccess(query);
    }
}
