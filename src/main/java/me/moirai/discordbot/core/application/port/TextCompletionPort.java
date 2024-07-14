package me.moirai.discordbot.core.application.port;

import me.moirai.discordbot.core.application.model.request.TextGenerationRequest;
import me.moirai.discordbot.core.application.model.result.TextGenerationResult;
import reactor.core.publisher.Mono;

public interface TextCompletionPort {

    Mono<TextGenerationResult> generateTextFrom(TextGenerationRequest request);
}
