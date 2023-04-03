package es.thalesalv.chatrpg.application.mapper.lorebook;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntryRegexEntity;
import es.thalesalv.chatrpg.domain.model.chconf.LorebookEntry;

@Component
public class LorebookEntryEntityToDTO implements Function<LorebookEntryRegexEntity, LorebookEntry> {

    @Override
    public LorebookEntry apply(LorebookEntryRegexEntity lorebookEntryEntity) {

        return LorebookEntry.builder()
                .id(lorebookEntryEntity.getLorebookEntry()
                        .getId())
                .description(lorebookEntryEntity.getLorebookEntry()
                        .getDescription())
                .playerDiscordId(lorebookEntryEntity.getLorebookEntry()
                        .getPlayerDiscordId())
                .name(lorebookEntryEntity.getLorebookEntry()
                        .getName())
                .regex(lorebookEntryEntity.getRegex())
                .regexId(lorebookEntryEntity.getId())
                .build();
    }
}
