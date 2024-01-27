package es.thalesalv.chatrpg.core.domain.world;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.common.exception.BusinessException;
import es.thalesalv.chatrpg.core.application.port.TokenizerPort;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LorebookEntryDomainServiceImpl implements LorebookEntryDomainService {

    @Value("${chatrpg.validation.token-limits.world.lorebook-entry.description}")
    private int lorebookEntryDescriptionTokenLimit;

    @Value("${chatrpg.validation.token-limits.world.lorebook-entry.name}")
    private int lorebookEntryNameTokenLimit;

    private final TokenizerPort tokenizerPort;

    @Override
    public LorebookEntry createLorebookEntry(String name, String description, String regex, String playerDiscordId) {

        LorebookEntry lorebookEntry = LorebookEntry.builder()
                .name(name)
                .regex(regex)
                .description(description)
                .playerDiscordId(playerDiscordId)
                .build();

        validateTokenCount(lorebookEntry);

        return lorebookEntry;
    }

    private void validateTokenCount(LorebookEntry lorebookEntry) {

        int lorebookEntryNameTokenCount = tokenizerPort.getTokenCountFrom(lorebookEntry.getName());
        if (lorebookEntryNameTokenCount > lorebookEntryNameTokenLimit) {
            throw new BusinessException("Amount of tokens in lorebook entry name surpasses allowed limit");
        }

        int lorebookEntryDescriptionTokenCount = tokenizerPort.getTokenCountFrom(lorebookEntry.getDescription());
        if (lorebookEntryDescriptionTokenCount > lorebookEntryDescriptionTokenLimit) {
            throw new BusinessException("Amount of tokens in lorebook entry description surpasses allowed limit");
        }
    }
}
