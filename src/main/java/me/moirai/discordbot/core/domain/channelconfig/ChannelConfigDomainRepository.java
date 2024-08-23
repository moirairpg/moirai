package me.moirai.discordbot.core.domain.channelconfig;

import java.util.Optional;

public interface ChannelConfigDomainRepository {

    Optional<ChannelConfig> findById(String id);

    ChannelConfig save(ChannelConfig channelConfig);

    void deleteById(String id);
}
