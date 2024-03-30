package es.thalesalv.chatrpg.core.domain.persona;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.common.exception.AssetAccessDeniedException;
import es.thalesalv.chatrpg.common.exception.AssetNotFoundException;
import es.thalesalv.chatrpg.common.exception.BusinessRuleViolationException;
import es.thalesalv.chatrpg.core.application.command.persona.CreatePersona;
import es.thalesalv.chatrpg.core.application.command.persona.DeletePersona;
import es.thalesalv.chatrpg.core.application.command.persona.UpdatePersona;
import es.thalesalv.chatrpg.core.application.query.persona.GetPersonaById;
import es.thalesalv.chatrpg.core.domain.CompletionRole;
import es.thalesalv.chatrpg.core.domain.Permissions;
import es.thalesalv.chatrpg.core.domain.Visibility;
import es.thalesalv.chatrpg.core.domain.port.TokenizerPort;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PersonaDomainServiceImpl implements PersonaDomainService {

    @Value("${chatrpg.validation.token-limits.persona.personality}")
    private int personalityTokenLimit;

    private final PersonaRepository repository;
    private final TokenizerPort tokenizerPort;

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

        validateTokenCount(command.getPersonality());

        Bump bump = Bump.builder()
                .content(command.getBumpContent())
                .frequency(command.getBumpFrequency())
                .role(CompletionRole.fromString(command.getBumpRole()))
                .build();

        Nudge nudge = Nudge.builder()
                .content(command.getNudgeContent())
                .role(CompletionRole.fromString(command.getNudgeRole()))
                .build();

        Permissions permissions = Permissions.builder()
                .ownerDiscordId(command.getRequesterDiscordId())
                .usersAllowedToRead(command.getReaderUsers())
                .usersAllowedToWrite(command.getWriterUsers())
                .build();

        Persona persona = Persona.builder()
                .name(command.getName())
                .personality(command.getPersonality())
                .visibility(Visibility.fromString(command.getVisibility()))
                .permissions(permissions)
                .nudge(nudge)
                .bump(bump)
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

        if (command.getBumpFrequency() != null) {
            persona.updateBumpFrequency(command.getBumpFrequency());
        }

        if (command.getVisibility().equalsIgnoreCase(Visibility.PUBLIC.name())) {
            persona.makePublic();
        } else if (command.getVisibility().equalsIgnoreCase(Visibility.PRIVATE.name())) {
            persona.makePrivate();
        }

        CollectionUtils.emptyIfNull(command.getReaderUsersToAdd())
                .forEach(persona::addReaderUser);

        CollectionUtils.emptyIfNull(command.getWriterUsersToAdd())
                .forEach(persona::addWriterUser);

        CollectionUtils.emptyIfNull(command.getReaderUsersToRemove())
                .forEach(persona::removeReaderUser);

        CollectionUtils.emptyIfNull(command.getWriterUsersToRemove())
                .forEach(persona::removeWriterUser);

        validateTokenCount(command.getPersonality());

        return repository.save(persona);
    }

    private void validateTokenCount(String personality) {

        int personalityTokenCount = tokenizerPort.getTokenCountFrom(personality);
        if (personalityTokenCount > personalityTokenLimit) {
            throw new BusinessRuleViolationException("Amount of tokens in personality surpasses allowed limit");
        }
    }
}
