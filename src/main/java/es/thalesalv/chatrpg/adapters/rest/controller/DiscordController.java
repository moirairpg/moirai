package es.thalesalv.chatrpg.adapters.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@CrossOrigin
@RestController
@RequestMapping("/discord")
public class DiscordController {

    @Value("${chatrpg.discord.api-token}")
    private String discordApiToken;

    private final WebClient webClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelConfigController.class);

    public DiscordController(@Value("${chatrpg.discord.api-base-url}") final String openAiBaseUrl,
            final WebClient.Builder webClientBuilder) {

        this.webClient = webClientBuilder.baseUrl(openAiBaseUrl)
                .build();
    }

    @GetMapping("/user/{user-id}")
    public Mono<String> retrieveUserInfo(@PathVariable(value = "user-id") final String userId) {

        LOGGER.info("Retrieving data of user with id {}", userId);
        return webClient.get()
                .uri("/users/" + userId)
                .headers(headers -> {
                    headers.add(HttpHeaders.AUTHORIZATION, "Bot " + discordApiToken);
                    headers.add(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
                })
                .retrieve()
                .bodyToMono(String.class);
    }

    @GetMapping("/user/self")
    public Mono<String> retrieveSelfUserInfo(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {

        LOGGER.info("Retrieving data of self user {}");
        return webClient.get()
                .uri("/users/@me")
                .headers(headers -> {
                    headers.add(HttpHeaders.AUTHORIZATION, authorization);
                    headers.add(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
                })
                .retrieve()
                .bodyToMono(String.class);
    }
}
