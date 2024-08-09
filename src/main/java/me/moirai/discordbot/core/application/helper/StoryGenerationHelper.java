package me.moirai.discordbot.core.application.helper;

import me.moirai.discordbot.infrastructure.outbound.adapter.request.StoryGenerationRequest;
import reactor.core.publisher.Mono;

public interface StoryGenerationHelper {

    Mono<Void> continueStory(StoryGenerationRequest useCase);
}
