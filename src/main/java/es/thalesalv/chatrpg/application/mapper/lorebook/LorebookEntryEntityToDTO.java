package es.thalesalv.chatrpg.application.mapper.lorebook;

import java.util.Optional;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntryEntity;
import es.thalesalv.chatrpg.domain.model.chconf.LorebookEntry;

@Component
public class LorebookEntryEntityToDTO implements Function<LorebookEntryEntity, LorebookEntry> {

    @Override
    public LorebookEntry apply(LorebookEntryEntity lorebookEntryEntity) {

        final String regex = Optional.ofNullable(lorebookEntryEntity.getRegex())
                .filter(StringUtils::isNotBlank)
                .orElse(lorebookEntryEntity.getName());

        return LorebookEntry.builder()
                .id(lorebookEntryEntity.getId())
                .description(lorebookEntryEntity.getDescription())
                .playerDiscordId(Optional.ofNullable(lorebookEntryEntity.getPlayerDiscordId())
                        .filter(StringUtils::isNotBlank)
                        .orElse(null))
                .name(lorebookEntryEntity.getName())
                .regex(regex)
                .build();
    }
}
