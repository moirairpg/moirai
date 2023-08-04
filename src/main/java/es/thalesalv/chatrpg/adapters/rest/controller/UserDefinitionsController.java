package es.thalesalv.chatrpg.adapters.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.thalesalv.chatrpg.application.service.UserDefinitionsService;
import es.thalesalv.chatrpg.domain.model.bot.UserDefinitions;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/userdef")
public class UserDefinitionsController {

    private final UserDefinitionsService userDefinitionsService;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDefinitionsController.class);

    @GetMapping
    public Mono<ResponseEntity<UserDefinitions>> retrieveUserDefinitions(
            @RequestHeader("requester") final String requesterUserId) {

        LOGGER.info("Received request to retrieve user definitions for user with ID -> {}", requesterUserId);
        final UserDefinitions userDefinitions = userDefinitionsService.retrieveUserDefinitions(requesterUserId);
        return Mono.just(buildResponse(userDefinitions));
    }

    @PutMapping("{user-id}")
    public Mono<ResponseEntity<UserDefinitions>> updateUserDefinitions(
            @RequestHeader("requester") final String requesterUserId,
            @PathVariable(value = "user-id") final String userId, @RequestBody final UserDefinitions userDefinitions) {

        LOGGER.info("Received request to update user definitions for user with ID -> {}", requesterUserId);
        final UserDefinitions updatedUserDefinitions = userDefinitionsService.updateUserDefinitions(userDefinitions);
        return Mono.just(buildResponse(updatedUserDefinitions));
    }

    private ResponseEntity<UserDefinitions> buildResponse(UserDefinitions userDefinitions) {

        LOGGER.info("Building response for userDefinitions -> {}", userDefinitions);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userDefinitions);
    }
}
