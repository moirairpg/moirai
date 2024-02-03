package es.thalesalv.chatrpg.core.domain.world;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.common.exception.BusinessRuleViolationException;
import es.thalesalv.chatrpg.core.domain.port.TokenizerPort;
import es.thalesalv.chatrpg.core.domain.world.World.Builder;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorldDomainServiceImpl implements WorldDomainService {

    @Value("${chatrpg.validation.token-limits.world.initial-prompt}")
    private int initialPromptTokenLimit;

    private final WorldRepository worldRepository;
    private final TokenizerPort tokenizerPort;

    @Override
    public World createWorld(Builder builder) {

        World world = builder.build();
        validateTokenCount(world);

        return worldRepository.save(world);
    }

    private void validateTokenCount(World world) {

        int initialPromptTokenCount = tokenizerPort.getTokenCountFrom(world.getInitialPrompt());
        if (initialPromptTokenCount > initialPromptTokenLimit) {
            throw new BusinessRuleViolationException("Amount of tokens in initial prompt surpasses allowed limit");
        }
    }
}
