package es.thalesalv.chatrpg.infrastructure.inbound.api.mapper;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.core.application.command.persona.CreatePersonaResult;
import es.thalesalv.chatrpg.core.application.command.persona.UpdatePersonaResult;
import es.thalesalv.chatrpg.core.application.query.persona.GetPersonaResult;
import es.thalesalv.chatrpg.core.application.query.persona.SearchPersonasResult;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.CreatePersonaResponse;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.PersonaResponse;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.SearchPersonasResponse;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.UpdatePersonaResponse;

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
                .nudgeContent(result.getNudgeContent())
                .nudgeRole(result.getNudgeRole())
                .bumpContent(result.getBumpContent())
                .bumpRole(result.getBumpRole())
                .bumpFrequency(result.getBumpFrequency())
                .visibility(result.getVisibility())
                .gameMode(result.getGameMode())
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
