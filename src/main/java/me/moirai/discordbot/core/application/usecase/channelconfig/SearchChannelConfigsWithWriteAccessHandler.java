package me.moirai.discordbot.core.application.usecase.channelconfig;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.usecase.channelconfig.request.SearchChannelConfigsWithWriteAccess;
import me.moirai.discordbot.core.application.usecase.channelconfig.result.SearchChannelConfigsResult;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfigRepository;

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
