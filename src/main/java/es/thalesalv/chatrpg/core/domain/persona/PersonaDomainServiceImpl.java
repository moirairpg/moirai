package es.thalesalv.chatrpg.core.domain.persona;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.common.exception.BusinessRuleViolationException;
import es.thalesalv.chatrpg.core.application.command.persona.CreatePersona;
import es.thalesalv.chatrpg.core.domain.CompletionRole;
import es.thalesalv.chatrpg.core.domain.Permissions;
import es.thalesalv.chatrpg.core.domain.Visibility;
import es.thalesalv.chatrpg.core.domain.port.TokenizerPort;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PersonaDomainServiceImpl implements PersonaDomainService {

    @Value("${chatrpg.validation.token-limits.persona.personality}")
    private int personalityTokenLimit;

    private final PersonaRepository repository;
    private final TokenizerPort tokenizerPort;

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
                .ownerDiscordId(command.getCreatorDiscordId())
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

    private void validateTokenCount(String personality) {

        int personalityTokenCount = tokenizerPort.getTokenCountFrom(personality);
        if (personalityTokenCount > personalityTokenLimit) {
            throw new BusinessRuleViolationException("Amount of tokens in personality surpasses allowed limit");
        }
    }
}
