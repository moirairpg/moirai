package es.thalesalv.chatrpg.application.mapper.lorebook;

import java.util.Optional;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntryEntity;
import es.thalesalv.chatrpg.domain.model.bot.LorebookEntry;

@Component
public class LorebookEntryDTOToEntity implements Function<LorebookEntry, LorebookEntryEntity> {

    @Override
    public LorebookEntryEntity apply(LorebookEntry lorebookEntry) {

        final String regex = Optional.ofNullable(lorebookEntry.getRegex())
                .filter(StringUtils::isNotBlank)
                .orElse(lorebookEntry.getName());

        return LorebookEntryEntity.builder()
                .id(lorebookEntry.getId())
                .name(lorebookEntry.getName())
                .regex(regex)
                .description(lorebookEntry.getDescription())
                .playerDiscordId(Optional.ofNullable(lorebookEntry.getPlayerDiscordId())
                        .filter(StringUtils::isNotBlank)
                        .orElse(null))
                .build();
    }
}
