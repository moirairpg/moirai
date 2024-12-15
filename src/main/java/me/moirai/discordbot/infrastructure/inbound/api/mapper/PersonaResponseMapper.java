package me.moirai.discordbot.infrastructure.inbound.api.mapper;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import me.moirai.discordbot.core.application.usecase.persona.result.CreatePersonaResult;
import me.moirai.discordbot.core.application.usecase.persona.result.GetPersonaResult;
import me.moirai.discordbot.core.application.usecase.persona.result.SearchPersonasResult;
import me.moirai.discordbot.core.application.usecase.persona.result.UpdatePersonaResult;
import me.moirai.discordbot.infrastructure.inbound.api.response.CreatePersonaResponse;
import me.moirai.discordbot.infrastructure.inbound.api.response.PersonaResponse;
import me.moirai.discordbot.infrastructure.inbound.api.response.SearchPersonasResponse;
import me.moirai.discordbot.infrastructure.inbound.api.response.UpdatePersonaResponse;

@Component
public class PersonaResponseMapper {

    public SearchPersonasResponse toResponse(SearchPersonasResult result) {

        List<PersonaResponse> personas = CollectionUtils.emptyIfNull(result.getResults())
                .stream()
                .map(this::toResponse)
                .toList();

        return SearchPersonasResponse.builder()
                .page(result.getPage())
                .resultsInPage(result.getItems())
                .totalPages(result.getTotalPages())
                .totalResults(result.getTotalItems())
                .results(personas)
                .build();
    }

    public PersonaResponse toResponse(GetPersonaResult result) {

        return PersonaResponse.builder()
                .id(result.getId())
                .name(result.getName())
                .personality(result.getPersonality())
                .creationDate(result.getCreationDate())
                .lastUpdateDate(result.getLastUpdateDate())
                .visibility(result.getVisibility())
                .ownerDiscordId(result.getOwnerDiscordId())
                .build();
    }

    public CreatePersonaResponse toResponse(CreatePersonaResult result) {

        return CreatePersonaResponse.build(result.getId());
    }

    public UpdatePersonaResponse toResponse(UpdatePersonaResult result) {

        return UpdatePersonaResponse.build(result.getLastUpdatedDateTime());
    }
}
