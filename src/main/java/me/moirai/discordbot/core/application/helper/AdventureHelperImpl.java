package me.moirai.discordbot.core.application.helper;

import me.moirai.discordbot.common.annotation.Helper;
import me.moirai.discordbot.core.application.port.AdventureQueryRepository;

@Helper
public class AdventureHelperImpl implements AdventureHelper {

    private final AdventureQueryRepository adventureQueryRepository;

    public AdventureHelperImpl(AdventureQueryRepository adventureQueryRepository) {
        this.adventureQueryRepository = adventureQueryRepository;
    }

    @Override
    public String getGameModeByDiscordChannelId(String channelId) {

        return adventureQueryRepository.getGameModeByDiscordChannelId(channelId);
    }
}
