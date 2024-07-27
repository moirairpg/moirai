package me.moirai.discordbot.core.application.port;

import java.util.Map;

import me.moirai.discordbot.core.domain.channelconfig.ModelConfiguration;
import reactor.core.publisher.Mono;

public interface StorySummarizationPort {

    Mono<Map<String, Object>> summarizeContextWith(Map<String, Object> context,
            ModelConfiguration modelConfiguration);
}
