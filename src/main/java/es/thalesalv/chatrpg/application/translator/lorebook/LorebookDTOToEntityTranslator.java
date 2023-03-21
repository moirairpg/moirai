package es.thalesalv.chatrpg.application.translator.lorebook;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.db.entity.LorebookEntryEntity;
import es.thalesalv.chatrpg.adapters.data.db.entity.LorebookRegexEntity;
import es.thalesalv.chatrpg.domain.model.openai.dto.LorebookEntry;

@Component
public class LorebookDTOToEntityTranslator implements Function<LorebookEntry, LorebookRegexEntity> {

    @Override
    public LorebookRegexEntity apply(LorebookEntry t) {

        return LorebookRegexEntity.builder()
                .id(t.getRegexId())
                .regex(t.getRegex())
                .lorebookEntry(LorebookEntryEntity.builder()
                        .id(t.getId())
                        .description(t.getDescription())
                        .name(t.getName())
                        .playerDiscordId(t.getPlayerDiscordId())
                        .build())
                .build();
    }
}
