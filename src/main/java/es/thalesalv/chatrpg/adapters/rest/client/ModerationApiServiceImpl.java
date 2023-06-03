package es.thalesalv.chatrpg.adapters.rest.client;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.reactive.function.client.WebClient;

import es.thalesalv.chatrpg.domain.model.openai.moderation.ModerationRequest;
import es.thalesalv.chatrpg.domain.model.openai.moderation.ModerationResponse;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Service
public class ModerationApiServiceImpl implements ModerationApiService {

    @Value("${chatrpg.openai.api-token}")
    private String openAiToken;

    @Value("${chatrpg.openai.moderation-uri}")
    private String moderationUri;

    @Value("${chatrpg.discord.retry.error-attempts}")
    private int errorAttemps;

    @Value("${chatrpg.discord.retry.error-delay}")
    private int errorDelay;

    private final WebClient webClient;

    private static final String BEARER = "Bearer ";
    private static final String RECEIVED_MODERATION_RESPONSE = "Received response from OpenAI moderation API -> {}";

    private static final Logger LOGGER = LoggerFactory.getLogger(ModerationApiServiceImpl.class);

    public ModerationApiServiceImpl(@Value("${chatrpg.openai.api-base-url}") final String openAiBaseUrl,
            final WebClient.Builder webClientBuilder) {

        this.webClient = webClientBuilder.baseUrl(openAiBaseUrl)
                .build();
    }

    @Override
    public Mono<ModerationResponse> callModeration(ModerationRequest request) {

        LOGGER.info("Making request to OpenAI moderation API -> {}", request);
        return webClient.post()
                .uri(moderationUri)
                .headers(headers -> {
                    headers.add(HttpHeaders.AUTHORIZATION, BEARER + openAiToken);
                    headers.add(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
                })
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ModerationResponse.class)
                .map(response -> {
                    LOGGER.info(RECEIVED_MODERATION_RESPONSE, response);
                    return response;
                })
                .retryWhen(Retry.fixedDelay(errorAttemps, Duration.ofSeconds(errorDelay)));
    }
}
