package es.thalesalv.gptbot.application.translator;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import es.thalesalv.gptbot.adapters.data.db.entity.LorebookRegex;
import es.thalesalv.gptbot.domain.model.openai.dto.LorebookDTO;

@Component
public class LorebookEntryToDTOTranslator implements Function<LorebookRegex, LorebookDTO> {

    @Override
    public LorebookDTO apply(LorebookRegex t) {

        return LorebookDTO.builder()
                .id(t.getLorebookEntry().getId())
                .description(t.getLorebookEntry().getDescription())
                .name(t.getLorebookEntry().getName())
                .regex(t.getRegex())
                .build();
    }
}
