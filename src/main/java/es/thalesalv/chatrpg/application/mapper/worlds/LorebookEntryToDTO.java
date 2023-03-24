package es.thalesalv.chatrpg.application.mapper.worlds;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.db.entity.LorebookRegexEntity;
import es.thalesalv.chatrpg.domain.model.openai.dto.LorebookEntry;

@Component
public class LorebookEntryToDTO implements Function<LorebookRegexEntity, LorebookEntry> {

    @Override
    public LorebookEntry apply(LorebookRegexEntity t) {

        return LorebookEntry.builder()
                .id(t.getLorebookEntry().getId())
                .description(t.getLorebookEntry().getDescription())
                .playerDiscordId(t.getLorebookEntry().getPlayerDiscordId())
                .name(t.getLorebookEntry().getName())
                .regex(t.getRegex())
                .build();
    }
}
