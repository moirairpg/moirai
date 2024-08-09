package me.moirai.discordbot.core.application.helper;

import java.util.List;
import java.util.Map;

import me.moirai.discordbot.core.application.usecase.discord.DiscordMessageData;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.ModelConfigurationRequest;

public interface LorebookEnrichmentHelper {

    Map<String, Object> enrichContextWithLorebook(List<DiscordMessageData> messagesExtracted, String worldId,
            ModelConfigurationRequest modelConfiguration);
}
