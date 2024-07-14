package me.moirai.discordbot.core.application.port;

import me.moirai.discordbot.core.application.model.result.TextModerationResult;
import reactor.core.publisher.Mono;

public interface TextModerationPort {

    Mono<TextModerationResult> moderate(String text);
}
