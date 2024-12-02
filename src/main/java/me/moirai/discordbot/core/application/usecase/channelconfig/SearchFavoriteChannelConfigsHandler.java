package me.moirai.discordbot.core.application.usecase.channelconfig;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.port.ChannelConfigQueryRepository;
import me.moirai.discordbot.core.application.usecase.channelconfig.request.SearchFavoriteChannelConfigs;
import me.moirai.discordbot.core.application.usecase.channelconfig.result.SearchChannelConfigsResult;

@UseCaseHandler
public class SearchFavoriteChannelConfigsHandler extends AbstractUseCaseHandler<SearchFavoriteChannelConfigs, SearchChannelConfigsResult> {

    private final ChannelConfigQueryRepository repository;

    public SearchFavoriteChannelConfigsHandler(ChannelConfigQueryRepository repository) {
        this.repository = repository;
    }

    @Override
    public SearchChannelConfigsResult execute(SearchFavoriteChannelConfigs query) {

        return repository.search(query);
    }
}
