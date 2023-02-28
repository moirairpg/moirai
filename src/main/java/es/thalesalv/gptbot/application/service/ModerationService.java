package es.thalesalv.gptbot.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import es.thalesalv.gptbot.adapters.rest.OpenAIApiService;
import es.thalesalv.gptbot.domain.exception.ModerationException;
import es.thalesalv.gptbot.domain.model.openai.moderation.ModerationRequest;
import es.thalesalv.gptbot.domain.model.openai.moderation.ModerationResponse;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ModerationService {

    private final OpenAIApiService openAIApiService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ModerationService.class);

    public Mono<ModerationResponse> moderate(String prompt) {

        final ModerationRequest request = ModerationRequest.builder().input(prompt).build();
        return openAIApiService.callModerationApi(request)
                .doOnNext(response -> {
                    if (response.getModerationResult().stream().anyMatch(result -> result.isFlagged())) {
                        LOGGER.warn("Unsafe content detected -> {}", prompt);
                        throw new ModerationException("Unsafe content detected", prompt);
                    }
                });
    }
}
