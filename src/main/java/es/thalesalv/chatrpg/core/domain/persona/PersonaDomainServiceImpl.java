package es.thalesalv.chatrpg.core.domain.persona;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.common.exception.BusinessRuleViolationException;
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
    public Persona createPersona(String name, String personality, Permissions permissions, Visibility visibility) {

        validateTokenCount(personality);

        Persona.Builder builder = Persona.builder();
        builder.name(name);
        builder.personality(personality);
        builder.permissions(permissions);
        builder.visibility(visibility);

        return repository.save(builder.build());
    }

    private void validateTokenCount(String personality) {

        int personalityTokenCount = tokenizerPort.getTokenCountFrom(personality);
        if (personalityTokenCount > personalityTokenLimit) {
            throw new BusinessRuleViolationException("Amount of tokens in personality surpasses allowed limit");
        }
    }
}
