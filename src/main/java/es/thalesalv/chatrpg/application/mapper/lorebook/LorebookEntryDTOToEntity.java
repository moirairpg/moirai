package es.thalesalv.chatrpg.application.mapper.lorebook;

import java.util.Optional;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntryEntity;
import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntryRegexEntity;
import es.thalesalv.chatrpg.domain.model.chconf.LorebookEntry;

@Component
public class LorebookEntryDTOToEntity implements Function<LorebookEntry, LorebookEntryRegexEntity> {

    @Override
    public LorebookEntryRegexEntity apply(LorebookEntry lorebookEntry) {

        final String regex = Optional.ofNullable(lorebookEntry.getRegex())
                .filter(StringUtils::isNotBlank)
                .orElse(lorebookEntry.getName());

        return LorebookEntryRegexEntity.builder()
                .id(lorebookEntry.getRegexId())
                .regex(regex)
                .lorebookEntry(LorebookEntryEntity.builder()
                        .id(lorebookEntry.getId())
                        .name(lorebookEntry.getName())
                        .description(lorebookEntry.getDescription())
                        .playerDiscordId(lorebookEntry.getPlayerDiscordId())
                        .build())
                .build();
    }
}
