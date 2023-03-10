package es.thalesalv.chatrpg.application.translator;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.db.entity.LorebookRegex;
import es.thalesalv.chatrpg.domain.model.openai.dto.LorebookDTO;

@Component
public class LorebookEntryToDTOTranslator implements Function<LorebookRegex, LorebookDTO> {

    @Override
    public LorebookDTO apply(LorebookRegex t) {

        return LorebookDTO.builder()
                .id(t.getLorebookEntry().getId())
                .description(t.getLorebookEntry().getDescription())
                .playerDiscordId(t.getLorebookEntry().getPlayerDiscordId())
                .name(t.getLorebookEntry().getName())
                .regex(t.getRegex())
                .build();
    }
}
