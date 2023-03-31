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
    public LorebookEntryRegexEntity apply(LorebookEntry t) {

        final String regex = Optional.ofNullable(t.getRegex())
                .filter(StringUtils::isNotBlank)
                .orElse(t.getName());

        return LorebookEntryRegexEntity.builder()
                .id(t.getRegexId())
                .regex(regex)
                .lorebookEntry(LorebookEntryEntity.builder()
                        .id(t.getId())
                        .name(t.getName())
                        .description(t.getDescription())
                        .playerDiscordId(t.getPlayerDiscordId())
                        .build())
                .build();
    }
}
