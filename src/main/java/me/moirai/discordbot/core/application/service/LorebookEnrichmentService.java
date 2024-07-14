package me.moirai.discordbot.core.application.service;

import java.util.Map;

import me.moirai.discordbot.core.domain.channelconfig.ModelConfiguration;
import reactor.core.publisher.Mono;

public interface LorebookEnrichmentService {

    Mono<Map<String, Object>> enrichContextWith(Map<String, Object> contextWithSummary, String worldId,
            ModelConfiguration modelConfiguration);
}
