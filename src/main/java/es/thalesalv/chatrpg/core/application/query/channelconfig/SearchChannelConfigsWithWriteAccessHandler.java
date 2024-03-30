package es.thalesalv.chatrpg.core.application.query.channelconfig;

import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.common.usecases.UseCaseHandler;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfigRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchChannelConfigsWithWriteAccessHandler extends UseCaseHandler<SearchChannelConfigsWithWriteAccess, SearchChannelConfigsResult> {

    private final ChannelConfigRepository repository;

    @Override
    public SearchChannelConfigsResult execute(SearchChannelConfigsWithWriteAccess query) {

        return repository.searchChannelConfigsWithWriteAccess(query);
    }
}
