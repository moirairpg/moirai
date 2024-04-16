package es.thalesalv.chatrpg.infrastructure.outbound.adapter;

import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import es.thalesalv.chatrpg.common.exception.DiscordApiException;
import es.thalesalv.chatrpg.common.exception.OpenAiApiException;
import es.thalesalv.chatrpg.core.application.model.request.TextGenerationRequest;
import es.thalesalv.chatrpg.core.application.model.request.TextModerationRequest;
import es.thalesalv.chatrpg.core.application.model.result.TextGenerationResult;
import es.thalesalv.chatrpg.core.application.model.result.TextModerationResult;
import es.thalesalv.chatrpg.core.application.port.OpenAiPort;
import es.thalesalv.chatrpg.infrastructure.outbound.adapter.request.ChatMessage;
import es.thalesalv.chatrpg.infrastructure.outbound.adapter.request.CompletionRequest;
import es.thalesalv.chatrpg.infrastructure.outbound.adapter.response.CompletionResponse;
import es.thalesalv.chatrpg.infrastructure.outbound.adapter.response.CompletionResponseError;
import es.thalesalv.chatrpg.infrastructure.outbound.adapter.response.ModerationResponse;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class OpenAiAdapter implements OpenAiPort {

    private static final String AUTHENTICATION_ERROR = "Error authenticating user on OpenAI";
    private static final String UNKNOWN_ERROR = "Error on OpenAI API";
    private static final String BAD_REQUEST_ERROR = "Bad request calling OpenAI API";

    private static final Predicate<HttpStatusCode> BAD_REQUEST = statusCode -> statusCode
            .isSameCodeAs(HttpStatusCode.valueOf(400));

    private static final Predicate<HttpStatusCode> UNAUTHORIZED = statusCode -> statusCode
            .isSameCodeAs(HttpStatusCode.valueOf(401));

    private final String moderationUrl;
    private final String completionsUri;
    private final WebClient webClient;

    public OpenAiAdapter(@Value("${chatrpg.openai.api.base-url}") String baseUrl,
            @Value("${chatrpg.openai.api.moderation-uri}") String moderationUrl,
            @Value("${chatrpg.openai.api.completions-uri}") String completionsUri,
            @Value("${chatrpg.openai.api.token}") String token,
            WebClient.Builder webClientBuilder) {

        this.moderationUrl = moderationUrl;
        this.completionsUri = completionsUri;
        this.webClient = webClientBuilder.baseUrl(baseUrl)
                .defaultHeaders(headers -> {
                    headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
                    headers.add(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
                })
                .build();
    }

    @Override
    public Mono<TextGenerationResult> generateTextFrom(TextGenerationRequest request) {

        return webClient.post()
                .uri(completionsUri)
                .bodyValue(toRequest(request))
                .retrieve()
                .onStatus(UNAUTHORIZED, this::handleUnauthorized)
                .onStatus(BAD_REQUEST, this::handleBadRequest)
                .onStatus(HttpStatusCode::isError, this::handleUnknownError)
                .bodyToMono(CompletionResponse.class)
                .map(this::toResult);
    }

    @Override
    public Mono<TextModerationResult> moderateTextFrom(TextModerationRequest request) {

        return webClient.post()
                .uri(moderationUrl)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ModerationResponse.class)
                .map(this::toResult);
    }

    private CompletionRequest toRequest(TextGenerationRequest request) {

        return CompletionRequest.builder()
                .frequencyPenalty(request.getFrequencyPenalty())
                .presencePenalty(request.getPresencePenalty())
                .temperature(request.getTemperature())
                .logitBias(request.getLogitBias())
                .stop(request.getStopSequences())
                .maxTokens(request.getMaxTokens())
                .model(request.getModel())
                .messages(request.getMessages()
                        .stream()
                        .map(message -> ChatMessage.build(message.getRole().name().toLowerCase(), message.getContent()))
                        .toList())
                .build();
    }

    private TextGenerationResult toResult(CompletionResponse response) {

        return TextGenerationResult.builder()
                .completionTokens(response.getUsage().getCompletionTokens())
                .promptTokens(response.getUsage().getPromptTokens())
                .totalTokens(response.getUsage().getTotalTokens())
                .outputText(response.getChoices().get(0).getMessage().getContent())
                .build();
    }

    private TextModerationResult toResult(ModerationResponse response) {

        return TextModerationResult.builder()
                .hasFlaggedContent(response.getResults().get(0).getFlagged())
                .moderationScores(response.getResults()
                        .get(0)
                        .getCategoryScores()
                        .entrySet()
                        .stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, entry -> Double.valueOf(entry.getValue()))))
                .build();
    }

    private Mono<? extends Throwable> handleUnauthorized(ClientResponse clientResponse) {

        return Mono.error(new DiscordApiException(HttpStatus.UNAUTHORIZED, AUTHENTICATION_ERROR));
    }

    private Mono<? extends Throwable> handleBadRequest(ClientResponse clientResponse) {

        return clientResponse.bodyToMono(CompletionResponseError.class)
                .map(resp -> {
                    log.error(BAD_REQUEST_ERROR + " -> {}", resp);
                    return new OpenAiApiException(HttpStatus.BAD_REQUEST, resp.getType(), resp.getMessage(),
                            String.format(BAD_REQUEST_ERROR, resp.getType(), resp.getMessage()));
                });
    }

    private Mono<? extends Throwable> handleUnknownError(ClientResponse clientResponse) {

        return clientResponse.bodyToMono(CompletionResponseError.class)
                .map(resp -> {
                    log.error(UNKNOWN_ERROR + " -> {}", resp);
                    return new OpenAiApiException(HttpStatus.INTERNAL_SERVER_ERROR, resp.getType(), resp.getMessage(),
                            String.format(UNKNOWN_ERROR, resp.getType(), resp.getMessage()));
                });
    }
}
