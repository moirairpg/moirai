package es.thalesalv.chatrpg.adapters.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.thalesalv.chatrpg.adapters.rest.DiscordApiService;
import es.thalesalv.chatrpg.application.service.DiscordAuthService;
import es.thalesalv.chatrpg.domain.model.DiscordUserData;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/discord")
public class DiscordController {

    @Value("${chatrpg.discord.api-token}")
    private String discordApiToken;

    private final DiscordAuthService discordAuthService;
    private final DiscordApiService discordApiService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelConfigController.class);

    @GetMapping("/user/{user-id}")
    public Mono<ResponseEntity<DiscordUserData>> retrieveUserInfo(
            @PathVariable(value = "user-id") final String userId) {

        LOGGER.info("Retrieving data of user with id {}", userId);
        return Mono.just(ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(discordAuthService.retrieveDiscordUserById(userId)));
    }

    @GetMapping("/user/self")
    public Mono<ResponseEntity<DiscordUserData>> retrieveSelfUserInfo(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {

        LOGGER.debug("Retrieving information of currently logged in user");
        return discordApiService.retrieveLoggedUser(authorization)
                .map(user -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(user));
    }
}
