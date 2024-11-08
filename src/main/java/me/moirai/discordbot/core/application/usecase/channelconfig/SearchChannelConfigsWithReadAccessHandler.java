package me.moirai.discordbot.core.application.usecase.channelconfig;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.port.ChannelConfigQueryRepository;
import me.moirai.discordbot.core.application.usecase.channelconfig.request.SearchChannelConfigsWithReadAccess;
import me.moirai.discordbot.core.application.usecase.channelconfig.result.SearchChannelConfigsResult;

@UseCaseHandler
public class SearchChannelConfigsWithReadAccessHandler extends AbstractUseCaseHandler<SearchChannelConfigsWithReadAccess, SearchChannelConfigsResult> {

    private final ChannelConfigQueryRepository repository;

    public SearchChannelConfigsWithReadAccessHandler(ChannelConfigQueryRepository repository) {
        this.repository = repository;
    }

    @Override
    public SearchChannelConfigsResult execute(SearchChannelConfigsWithReadAccess query) {

        return repository.searchChannelConfigsWithReadAccess(query);
    }
}
