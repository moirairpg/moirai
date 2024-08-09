package me.moirai.discordbot.infrastructure.inbound.api.mapper;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import me.moirai.discordbot.core.application.usecase.persona.request.CreatePersona;
import me.moirai.discordbot.core.application.usecase.persona.request.DeletePersona;
import me.moirai.discordbot.core.application.usecase.persona.request.UpdatePersona;
import me.moirai.discordbot.infrastructure.inbound.api.request.CreatePersonaRequest;
import me.moirai.discordbot.infrastructure.inbound.api.request.UpdatePersonaRequest;

@Component
public class PersonaRequestMapper {

    public CreatePersona toCommand(CreatePersonaRequest request, String requesterDiscordId) {

        return CreatePersona.builder()
                .name(request.getName())
                .personality(request.getPersonality())
                .nudgeContent(request.getNudgeContent())
                .nudgeRole(request.getNudgeRole())
                .bumpContent(request.getBumpContent())
                .bumpRole(request.getBumpRole())
                .bumpFrequency(request.getBumpFrequency())
                .visibility(request.getVisibility())
                .requesterDiscordId(requesterDiscordId)
                .usersAllowedToRead(CollectionUtils.emptyIfNull(request.getUsersAllowedToRead())
                        .stream()
                        .toList())
                .usersAllowedToWrite(CollectionUtils.emptyIfNull(request.getUsersAllowedToWrite())
                        .stream()
                        .toList())
                .build();
    }

    public UpdatePersona toCommand(UpdatePersonaRequest request, String personaId, String requesterDiscordId) {

        return UpdatePersona.builder()
                .id(personaId)
                .name(request.getName())
                .personality(request.getPersonality())
                .nudgeContent(request.getNudgeContent())
                .nudgeRole(request.getNudgeRole())
                .bumpContent(request.getBumpContent())
                .bumpRole(request.getBumpRole())
                .bumpFrequency(request.getBumpFrequency())
                .visibility(request.getVisibility())
                .requesterDiscordId(requesterDiscordId)
                .usersAllowedToWriteToAdd(CollectionUtils.emptyIfNull(request.getUsersAllowedToWriteToAdd())
                        .stream()
                        .toList())
                .usersAllowedToReadToAdd(CollectionUtils.emptyIfNull(request.getUsersAllowedToReadToAdd())
                        .stream()
                        .toList())
                .usersAllowedToWriteToRemove(CollectionUtils.emptyIfNull(request.getUsersAllowedToWriteToRemove())
                        .stream()
                        .toList())
                .usersAllowedToReadToRemove(CollectionUtils.emptyIfNull(request.getUsersAllowedToReadToRemove())
                        .stream()
                        .toList())
                .build();
    }

    public DeletePersona toCommand(String personaId, String requesterId) {

        return DeletePersona.build(personaId, requesterId);
    }
}
