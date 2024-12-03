package me.moirai.discordbot.core.application.usecase.channelconfig;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.port.ChannelConfigQueryRepository;
import me.moirai.discordbot.core.application.usecase.channelconfig.request.SearchChannelConfigsWithWriteAccess;
import me.moirai.discordbot.core.application.usecase.channelconfig.result.SearchChannelConfigsResult;

@UseCaseHandler
public class SearchChannelConfigsWithWriteAccessHandler extends AbstractUseCaseHandler<SearchChannelConfigsWithWriteAccess, SearchChannelConfigsResult> {

    private final ChannelConfigQueryRepository repository;

    public SearchChannelConfigsWithWriteAccessHandler(ChannelConfigQueryRepository repository) {
        this.repository = repository;
    }

    @Override
    public SearchChannelConfigsResult execute(SearchChannelConfigsWithWriteAccess query) {

        return repository.search(query);
    }
}
