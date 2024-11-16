package me.moirai.discordbot.infrastructure.inbound.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import me.moirai.discordbot.common.usecases.UseCaseRunner;
import me.moirai.discordbot.common.web.SecurityContextAware;
import me.moirai.discordbot.core.application.usecase.completion.request.CompleteText;
import me.moirai.discordbot.core.application.usecase.completion.result.CompleteTextResult;
import me.moirai.discordbot.infrastructure.inbound.api.request.TextCompletionRequest;
import me.moirai.discordbot.infrastructure.inbound.api.response.TextCompletionResponse;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/text-completion")
@Tag(name = "Text completion", description = "Endpoints for text generation in MoirAI")
public class CompletionController extends SecurityContextAware {

    private final UseCaseRunner useCaseRunner;

    public CompletionController(UseCaseRunner useCaseRunner) {
        this.useCaseRunner = useCaseRunner;
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.OK)
    public Mono<TextCompletionResponse> generateText(@Valid @RequestBody TextCompletionRequest request) {

        return flatMapWithAuthenticatedUser(authenticatedUser -> {

            CompleteText command = CompleteText.builder()
                    .messages(request.getMessages().stream()
                            .map(this::mapMessages)
                            .toList())
                    .temperature(request.getTemperature())
                    .aiModel(request.getAiModel())
                    .personaId(request.getPersonaId())
                    .worldId(request.getWorldId())
                    .maxTokenLimit(request.getMaxTokenLimit())
                    .frequencyPenalty(request.getFrequencyPenalty())
                    .presencePenalty(request.getPresencePenalty())
                    .logitBias(request.getLogitBias())
                    .stopSequences(request.getStopSequences())
                    .moderationLevel(request.getModerationLevel())
                    .authorDiscordId(authenticatedUser.getId())
                    .build();

            return useCaseRunner.run(command)
                    .map(this::toResponse);
        });
    }

    private CompleteText.Message mapMessages(TextCompletionRequest.Message message) {

        return CompleteText.Message.builder()
                .isAuthorBot(message.getIsAuthorBot())
                .messageContent(message.getMessageContent())
                .build();
    }

    private TextCompletionResponse toResponse(CompleteTextResult result) {

        return TextCompletionResponse.builder()
                .outputText(result.getOutputText())
                .completionTokens(result.getCompletionTokens())
                .promptTokens(result.getPromptTokens())
                .totalTokens(result.getTotalTokens())
                .completionTokens(result.getCompletionTokens())
                .tokenIds(result.getTokenIds())
                .tokens(result.getTokens())
                .build();
    }
}
