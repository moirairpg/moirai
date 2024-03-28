package es.thalesalv.chatrpg.infrastructure.inbound.api.mapper;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.core.application.command.persona.CreatePersona;
import es.thalesalv.chatrpg.core.application.command.persona.DeletePersona;
import es.thalesalv.chatrpg.core.application.command.persona.UpdatePersona;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.CreatePersonaRequest;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.UpdatePersonaRequest;

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
                .readerUsers(CollectionUtils.emptyIfNull(request.getReaderUsers())
                        .stream()
                        .toList())
                .writerUsers(CollectionUtils.emptyIfNull(request.getWriterUsers())
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
                .writerUsersToAdd(CollectionUtils.emptyIfNull(request.getWriterUsersToAdd())
                        .stream()
                        .toList())
                .readerUsersToAdd(CollectionUtils.emptyIfNull(request.getReaderUsersToAdd())
                        .stream()
                        .toList())
                .writerUsersToRemove(CollectionUtils.emptyIfNull(request.getWriterUsersToRemove())
                        .stream()
                        .toList())
                .readerUsersToRemove(CollectionUtils.emptyIfNull(request.getReaderUsersToRemove())
                        .stream()
                        .toList())
                .build();
    }

    public DeletePersona toCommand(String personaId, String requesterId) {

        return DeletePersona.build(personaId, requesterId);
    }
}
