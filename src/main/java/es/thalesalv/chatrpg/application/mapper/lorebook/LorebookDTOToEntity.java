package es.thalesalv.chatrpg.application.mapper.lorebook;

import java.util.Optional;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.db.entity.LorebookEntryEntity;
import es.thalesalv.chatrpg.adapters.data.db.entity.LorebookRegexEntity;
import es.thalesalv.chatrpg.domain.model.openai.dto.LorebookEntry;

@Component
public class LorebookDTOToEntity implements Function<LorebookEntry, LorebookRegexEntity> {

    @Override
    public LorebookRegexEntity apply(LorebookEntry t) {

        final String regex = Optional.ofNullable(t.getRegex())
                .filter(StringUtils::isNotBlank).orElse(t.getName());

        return LorebookRegexEntity.builder()
                .id(t.getRegexId())
                .regex(regex)
                .lorebookEntry(LorebookEntryEntity.builder()
                        .id(t.getId())
                        .description(t.getDescription())
                        .name(t.getName())
                        .playerDiscordId(t.getPlayerDiscordId())
                        .build())
                .build();
    }
}
