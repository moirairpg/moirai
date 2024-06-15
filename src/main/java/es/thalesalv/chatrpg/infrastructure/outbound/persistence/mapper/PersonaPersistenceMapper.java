package es.thalesalv.chatrpg.infrastructure.outbound.persistence.mapper;

import static org.apache.commons.lang3.StringUtils.isBlank;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.core.application.usecase.persona.result.GetPersonaResult;
import es.thalesalv.chatrpg.core.application.usecase.persona.result.SearchPersonasResult;
import es.thalesalv.chatrpg.core.domain.CompletionRole;
import es.thalesalv.chatrpg.core.domain.Permissions;
import es.thalesalv.chatrpg.core.domain.Visibility;
import es.thalesalv.chatrpg.core.domain.persona.Bump;
import es.thalesalv.chatrpg.core.domain.persona.GameMode;
import es.thalesalv.chatrpg.core.domain.persona.Nudge;
import es.thalesalv.chatrpg.core.domain.persona.Persona;
import es.thalesalv.chatrpg.infrastructure.outbound.persistence.persona.BumpEntity;
import es.thalesalv.chatrpg.infrastructure.outbound.persistence.persona.NudgeEntity;
import es.thalesalv.chatrpg.infrastructure.outbound.persistence.persona.PersonaEntity;

@Component
public class PersonaPersistenceMapper {

    public PersonaEntity mapToEntity(Persona persona) {

        String creatorOrOwnerDiscordId = isBlank(persona.getCreatorDiscordId())
                ? persona.getOwnerDiscordId()
                : persona.getCreatorDiscordId();

        PersonaEntity.Builder personaBuilder = PersonaEntity.builder();
        if (persona.getBump() != null) {
            BumpEntity bump = BumpEntity.builder()
                    .content(persona.getBump().getContent())
                    .role(persona.getBump().getRole().toString())
                    .frequency(persona.getBump().getFrequency())
                    .build();

            personaBuilder.bump(bump);
        }

        if (persona.getNudge() != null) {
            NudgeEntity nudge = NudgeEntity.builder()
                    .content(persona.getNudge().getContent())
                    .role(persona.getNudge().getRole().toString())
                    .build();

            personaBuilder.nudge(nudge);
        }

        return personaBuilder.id(persona.getId())
                .name(persona.getName())
                .personality(persona.getPersonality())
                .visibility(persona.getVisibility().toString())
                .ownerDiscordId(persona.getOwnerDiscordId())
                .gameMode(persona.getGameMode().name())
                .usersAllowedToRead(persona.getUsersAllowedToRead())
                .usersAllowedToWrite(persona.getUsersAllowedToWrite())
                .creatorDiscordId(creatorOrOwnerDiscordId)
                .creationDate(persona.getCreationDate())
                .lastUpdateDate(persona.getLastUpdateDate())
                .build();
    }

    public Persona mapFromEntity(PersonaEntity persona) {

        Persona.Builder personaBuilder = Persona.builder();
        if (persona.getBump() != null) {
            Bump bump = Bump.builder()
                    .content(persona.getBump().getContent())
                    .role(CompletionRole.fromString(persona.getBump().getRole()))
                    .frequency(persona.getBump().getFrequency())
                    .build();

            personaBuilder.bump(bump);
        }

        if (persona.getNudge() != null) {
            Nudge nudge = Nudge.builder()
                    .content(persona.getNudge().getContent())
                    .role(CompletionRole.fromString(persona.getNudge().getRole()))
                    .build();

            personaBuilder.nudge(nudge);
        }

        Permissions permissions = Permissions.builder()
                .ownerDiscordId(persona.getOwnerDiscordId())
                .usersAllowedToRead(persona.getUsersAllowedToRead())
                .usersAllowedToWrite(persona.getUsersAllowedToWrite())
                .build();

        return personaBuilder.id(persona.getId())
                .name(persona.getName())
                .personality(persona.getPersonality())
                .visibility(Visibility.fromString(persona.getVisibility()))
                .permissions(permissions)
                .gameMode(GameMode.fromString(persona.getGameMode()))
                .creationDate(persona.getCreationDate())
                .lastUpdateDate(persona.getLastUpdateDate())
                .creatorDiscordId(persona.getCreatorDiscordId())
                .build();
    }

    public GetPersonaResult mapToResult(PersonaEntity persona) {

        return GetPersonaResult.builder()
                .id(persona.getId())
                .name(persona.getName())
                .personality(persona.getPersonality())
                .visibility(persona.getVisibility())
                .gameMode(persona.getGameMode())
                .usersAllowedToRead(persona.getUsersAllowedToRead())
                .usersAllowedToWrite(persona.getUsersAllowedToWrite())
                .creationDate(persona.getCreationDate())
                .lastUpdateDate(persona.getLastUpdateDate())
                .ownerDiscordId(persona.getOwnerDiscordId())
                .build();
    }

    public SearchPersonasResult mapToResult(Page<PersonaEntity> pagedResult) {

        return SearchPersonasResult.builder()
                .results(pagedResult.getContent()
                        .stream()
                        .map(this::mapToResult)
                        .toList())
                .page(pagedResult.getNumber() + 1)
                .items(pagedResult.getNumberOfElements())
                .totalItems(pagedResult.getTotalElements())
                .totalPages(pagedResult.getTotalPages())
                .build();
    }
}
