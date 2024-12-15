package me.moirai.discordbot.core.application.usecase.persona;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.exception.AssetAccessDeniedException;
import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.port.PersonaQueryRepository;
import me.moirai.discordbot.core.application.usecase.persona.request.GetPersonaById;
import me.moirai.discordbot.core.application.usecase.persona.result.GetPersonaResult;
import me.moirai.discordbot.core.domain.persona.Persona;

@UseCaseHandler
public class GetPersonaByIdHandler extends AbstractUseCaseHandler<GetPersonaById, GetPersonaResult> {

    private static final String PERSONA_NOT_FOUND = "Persona was not found";
    private static final String PERMISSION_VIEW_DENIED = "User does not have permission to view this persona";

    private final PersonaQueryRepository queryRepository;

    public GetPersonaByIdHandler(PersonaQueryRepository queryRepository) {
        this.queryRepository = queryRepository;
    }

    @Override
    public GetPersonaResult execute(GetPersonaById query) {

        Persona persona = queryRepository.findById(query.getId())
                .orElseThrow(() -> new AssetNotFoundException(PERSONA_NOT_FOUND));

        if (!persona.canUserRead(query.getRequesterDiscordId())) {
            throw new AssetAccessDeniedException(PERMISSION_VIEW_DENIED);
        }

        return mapResult(persona);
    }

    private GetPersonaResult mapResult(Persona persona) {

        return GetPersonaResult.builder()
                .id(persona.getId())
                .name(persona.getName())
                .personality(persona.getName())
                .visibility(persona.getVisibility().name())
                .creationDate(persona.getCreationDate())
                .lastUpdateDate(persona.getLastUpdateDate())
                .ownerDiscordId(persona.getOwnerDiscordId())
                .usersAllowedToRead(persona.getUsersAllowedToRead())
                .usersAllowedToWrite(persona.getUsersAllowedToWrite())
                .build();
    }
}
