package es.thalesalv.chatrpg.application.mapper.lorebook;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntryRegexEntity;
import es.thalesalv.chatrpg.domain.model.chconf.LorebookEntry;

@Component
public class LorebookEntryEntityToDTO implements Function<LorebookEntryRegexEntity, LorebookEntry> {

    @Override
    public LorebookEntry apply(LorebookEntryRegexEntity t) {

        return LorebookEntry.builder()
                .id(t.getLorebookEntry().getId())
                .description(t.getLorebookEntry().getDescription())
                .playerDiscordId(t.getLorebookEntry().getPlayerDiscordId())
                .name(t.getLorebookEntry().getName())
                .regex(t.getRegex())
                .regexId(t.getId())
                .build();
    }
}
