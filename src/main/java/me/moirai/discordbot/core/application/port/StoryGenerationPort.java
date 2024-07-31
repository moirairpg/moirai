package me.moirai.discordbot.core.application.port;

import me.moirai.discordbot.infrastructure.outbound.adapter.request.StoryGenerationRequest;
import reactor.core.publisher.Mono;

public interface StoryGenerationPort {

    Mono<Void> continueStory(StoryGenerationRequest useCase);
}
