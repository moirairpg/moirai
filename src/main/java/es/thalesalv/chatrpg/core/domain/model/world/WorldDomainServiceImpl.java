package es.thalesalv.chatrpg.core.domain.model.world;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.common.exception.BusinessException;
import es.thalesalv.chatrpg.core.application.port.TokenizerPort;
import es.thalesalv.chatrpg.core.domain.model.world.World.Builder;
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
            throw new BusinessException("Amount of tokens in initial prompt surpasses allowed limit");
        }
    }
}
