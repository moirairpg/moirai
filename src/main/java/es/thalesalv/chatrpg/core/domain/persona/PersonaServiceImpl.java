package es.thalesalv.chatrpg.core.domain.persona;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import es.thalesalv.chatrpg.common.annotation.DomainService;
import es.thalesalv.chatrpg.common.exception.AssetAccessDeniedException;
import es.thalesalv.chatrpg.common.exception.AssetNotFoundException;
import es.thalesalv.chatrpg.core.application.command.persona.CreatePersona;
import es.thalesalv.chatrpg.core.application.command.persona.DeletePersona;
import es.thalesalv.chatrpg.core.application.command.persona.UpdatePersona;
import es.thalesalv.chatrpg.core.application.query.persona.GetPersonaById;
import es.thalesalv.chatrpg.core.domain.CompletionRole;
import es.thalesalv.chatrpg.core.domain.Permissions;
import es.thalesalv.chatrpg.core.domain.Visibility;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PersonaServiceImpl implements PersonaService {

    private final PersonaRepository repository;

    @Override
    public Persona getPersonaById(GetPersonaById query) {

        Persona persona = repository.findById(query.getId())
                .orElseThrow(() -> new AssetNotFoundException("Persona to be viewed was not found"));

        if (!persona.canUserRead(query.getRequesterDiscordId())) {
            throw new AssetAccessDeniedException("User does not have permission to view this persona");
        }

        return persona;
    }

    @Override
    public Persona getPersonaById(String id) {

        Persona persona = repository.findById(id)
                .orElseThrow(() -> new AssetNotFoundException("Persona to be viewed was not found"));

        return persona;
    }

    @Override
    public void deletePersona(DeletePersona command) {

        Persona persona = repository.findById(command.getId())
                .orElseThrow(() -> new AssetNotFoundException("Persona to be deleted was not found"));

        if (!persona.canUserWrite(command.getRequesterDiscordId())) {
            throw new AssetAccessDeniedException("User does not have permission to modify this persona");
        }

        repository.deleteById(command.getId());
    }

    @Override
    public Persona createFrom(CreatePersona command) {

        Persona.Builder personaBuilder = Persona.builder();
        if (StringUtils.isNotBlank(command.getBumpContent())) {
            Bump bump = Bump.builder()
                    .content(command.getBumpContent())
                    .frequency(command.getBumpFrequency())
                    .role(CompletionRole.fromString(command.getBumpRole()))
                    .build();

            personaBuilder.bump(bump);
        }

        if (StringUtils.isNotBlank(command.getNudgeContent())) {
            Nudge nudge = Nudge.builder()
                    .content(command.getNudgeContent())
                    .role(CompletionRole.fromString(command.getNudgeRole()))
                    .build();

            personaBuilder.nudge(nudge);
        }

        Permissions permissions = Permissions.builder()
                .ownerDiscordId(command.getRequesterDiscordId())
                .usersAllowedToRead(command.getUsersAllowedToRead())
                .usersAllowedToWrite(command.getUsersAllowedToWrite())
                .build();

        Persona persona = personaBuilder.name(command.getName())
                .personality(command.getPersonality())
                .visibility(Visibility.fromString(command.getVisibility()))
                .permissions(permissions)
                .gameMode(GameMode.fromString(command.getGameMode()))
                .build();

        return repository.save(persona);
    }

    @Override
    public Persona update(UpdatePersona command) {

        Persona persona = repository.findById(command.getId())
                .orElseThrow(() -> new AssetNotFoundException("Persona to be updated was not found"));

        if (!persona.canUserWrite(command.getRequesterDiscordId())) {
            throw new AssetAccessDeniedException("User does not have permission to modify this persona");
        }

        if (StringUtils.isNotBlank(command.getName())) {
            persona.updateName(command.getName());
        }

        if (StringUtils.isNotBlank(command.getPersonality())) {
            persona.updatePersonality(command.getPersonality());
        }

        if (StringUtils.isNotBlank(command.getNudgeRole())) {
            persona.updateNudgeRole(CompletionRole.fromString(command.getNudgeRole()));
        }

        if (StringUtils.isNotBlank(command.getNudgeContent())) {
            persona.updateNudgeContent(command.getNudgeContent());
        }

        if (StringUtils.isNotBlank(command.getBumpRole())) {
            persona.updateBumpRole(CompletionRole.fromString(command.getBumpRole()));
        }

        if (StringUtils.isNotBlank(command.getBumpContent())) {
            persona.updateBumpContent(command.getBumpContent());
        }

        if (StringUtils.isNotBlank(command.getGameMode())) {
            persona.updateGameMode(GameMode.fromString(command.getGameMode()));
        }

        if (command.getBumpFrequency() != null) {
            persona.updateBumpFrequency(command.getBumpFrequency());
        }

        if (StringUtils.isNotBlank(command.getVisibility())) {
            if (command.getVisibility().equalsIgnoreCase(Visibility.PUBLIC.name())) {
                persona.makePublic();
            } else if (command.getVisibility().equalsIgnoreCase(Visibility.PRIVATE.name())) {
                persona.makePrivate();
            }
        }

        CollectionUtils.emptyIfNull(command.getUsersAllowedToReadToAdd())
                .stream()
                .filter(userId -> !persona.canUserRead(userId))
                .forEach(persona::addReaderUser);

        CollectionUtils.emptyIfNull(command.getUsersAllowedToWriteToAdd())
                .stream()
                .filter(userId -> !persona.canUserWrite(userId))
                .forEach(persona::addWriterUser);

        CollectionUtils.emptyIfNull(command.getUsersAllowedToReadToRemove())
                .forEach(persona::removeReaderUser);

        CollectionUtils.emptyIfNull(command.getUsersAllowedToWriteToRemove())
                .forEach(persona::removeWriterUser);

        return repository.save(persona);
    }
}
