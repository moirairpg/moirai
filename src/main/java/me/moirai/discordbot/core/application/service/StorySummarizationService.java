package me.moirai.discordbot.core.application.service;

import java.util.List;
import java.util.Map;

import me.moirai.discordbot.core.domain.channelconfig.ModelConfiguration;
import me.moirai.discordbot.infrastructure.outbound.adapter.response.ChatMessageData;
import reactor.core.publisher.Mono;

public interface StorySummarizationService {

    Mono<Map<String, Object>> summarizeContextWith(List<ChatMessageData> messagesExtracted,
            ModelConfiguration modelConfiguration);
}
