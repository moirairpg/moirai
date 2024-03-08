package es.thalesalv.chatrpg.core.application.query.channelconfig;

import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.common.cqrs.query.QueryHandler;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfigRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchChannelConfigsHandler extends QueryHandler<SearchChannelConfigs, SearchChannelConfigsResult> {

    private final ChannelConfigRepository repository;

    @Override
    public SearchChannelConfigsResult handle(SearchChannelConfigs query) {

        return repository.searchChannelConfigs(query);
    }
}
