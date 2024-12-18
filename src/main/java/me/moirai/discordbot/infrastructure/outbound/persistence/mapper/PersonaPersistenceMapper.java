package me.moirai.discordbot.infrastructure.outbound.persistence.mapper;

import static org.apache.commons.lang3.StringUtils.isBlank;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import me.moirai.discordbot.core.application.usecase.persona.result.GetPersonaResult;
import me.moirai.discordbot.core.application.usecase.persona.result.SearchPersonasResult;
import me.moirai.discordbot.core.domain.Permissions;
import me.moirai.discordbot.core.domain.Visibility;
import me.moirai.discordbot.core.domain.persona.Persona;
import me.moirai.discordbot.infrastructure.outbound.persistence.persona.PersonaEntity;

@Component
public class PersonaPersistenceMapper {

    public PersonaEntity mapToEntity(Persona persona) {

        String creatorOrOwnerDiscordId = isBlank(persona.getCreatorDiscordId())
                ? persona.getOwnerDiscordId()
                : persona.getCreatorDiscordId();

        return PersonaEntity.builder()
                .id(persona.getId())
                .name(persona.getName())
                .personality(persona.getPersonality())
                .visibility(persona.getVisibility().toString())
                .ownerDiscordId(persona.getOwnerDiscordId())
                .usersAllowedToRead(persona.getUsersAllowedToRead())
                .usersAllowedToWrite(persona.getUsersAllowedToWrite())
                .creatorDiscordId(creatorOrOwnerDiscordId)
                .creationDate(persona.getCreationDate())
                .lastUpdateDate(persona.getLastUpdateDate())
                .version(persona.getVersion())
                .build();
    }

    public Persona mapFromEntity(PersonaEntity persona) {

        Permissions permissions = Permissions.builder()
                .ownerDiscordId(persona.getOwnerDiscordId())
                .usersAllowedToRead(persona.getUsersAllowedToRead())
                .usersAllowedToWrite(persona.getUsersAllowedToWrite())
                .build();

        return Persona.builder().id(persona.getId())
                .name(persona.getName())
                .personality(persona.getPersonality())
                .visibility(Visibility.fromString(persona.getVisibility()))
                .permissions(permissions)
                .creationDate(persona.getCreationDate())
                .lastUpdateDate(persona.getLastUpdateDate())
                .creatorDiscordId(persona.getCreatorDiscordId())
                .version(persona.getVersion())
                .build();
    }

    public GetPersonaResult mapToResult(PersonaEntity persona) {

        return GetPersonaResult.builder()
                .id(persona.getId())
                .name(persona.getName())
                .personality(persona.getPersonality())
                .visibility(persona.getVisibility())
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
