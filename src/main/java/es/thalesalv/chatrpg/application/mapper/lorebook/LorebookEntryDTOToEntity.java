package es.thalesalv.chatrpg.application.mapper.lorebook;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntryEntity;
import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntryRegexEntity;
import es.thalesalv.chatrpg.domain.model.chconf.LorebookEntry;

@Component
public class LorebookEntryDTOToEntity implements Function<LorebookEntry, LorebookEntryRegexEntity> {

    @Override
    public LorebookEntryRegexEntity apply(LorebookEntry t) {

        return LorebookEntryRegexEntity.builder()
                .id(t.getRegexId())
                .regex(t.getRegex())
                .lorebookEntry(LorebookEntryEntity.builder()
                        .id(t.getId())
                        .name(t.getName())
                        .description(t.getDescription())
                        .playerDiscordId(t.getPlayerDiscordId())
                        .build())
                .build();
    }
}
