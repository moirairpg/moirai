package es.thalesalv.chatrpg.core.application.query.channelconfig;

import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.common.usecases.UseCaseHandler;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfigRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchChannelConfigsHandler extends UseCaseHandler<SearchChannelConfigs, SearchChannelConfigsResult> {

    private final ChannelConfigRepository repository;

    @Override
    public SearchChannelConfigsResult execute(SearchChannelConfigs query) {

        return repository.searchChannelConfigs(query, "owner");
    }
}
