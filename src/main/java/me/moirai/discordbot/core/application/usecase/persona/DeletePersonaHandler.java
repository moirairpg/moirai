package me.moirai.discordbot.core.application.usecase.persona;

import org.apache.commons.lang3.StringUtils;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.exception.AssetAccessDeniedException;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.usecase.persona.request.DeletePersona;
import me.moirai.discordbot.core.domain.persona.Persona;
import me.moirai.discordbot.core.domain.persona.PersonaService;

@UseCaseHandler
public class DeletePersonaHandler extends AbstractUseCaseHandler<DeletePersona, Void> {

    private static final String ID_CANNOT_BE_NULL_OR_EMPTY = "Persona ID cannot be null or empty";
    private static final String PERMISSION_MODIFY_DENIED = "User does not have permission to modify this persona";

    private final PersonaService domainService;

    public DeletePersonaHandler(PersonaService domainService) {

        this.domainService = domainService;
    }

    @Override
    public void validate(DeletePersona command) {

        if (StringUtils.isBlank(command.getId())) {
            throw new IllegalArgumentException(ID_CANNOT_BE_NULL_OR_EMPTY);
        }
    }

    @Override
    public Void execute(DeletePersona command) {

        Persona persona = domainService.getById(command.getId());
        if (!persona.canUserWrite(command.getRequesterDiscordId())) {
            throw new AssetAccessDeniedException(PERMISSION_MODIFY_DENIED);
        }

        domainService.delete(command);

        return null;
    }
}
