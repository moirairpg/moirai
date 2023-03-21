package es.thalesalv.chatrpg.application.translator.worlds;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.db.entity.WorldEntity;
import es.thalesalv.chatrpg.application.translator.lorebook.LorebookDTOToEntityTranslator;
import es.thalesalv.chatrpg.domain.model.openai.dto.World;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WorldDTOToEntityTranslator implements Function<World, WorldEntity> {

    private final LorebookDTOToEntityTranslator lorebookDTOToEntityTranslator;

    @Override
    public WorldEntity apply(World t) {

        return WorldEntity.builder()
                .editPermissions(t.getEditPermissions())
                .id(t.getId())
                .initialPrompt(t.getInitialPrompt())
                .name(t.getName())
                .owner(t.getOwner())
                .visibility(t.getVisibility())
                .description(t.getDescription())
                .lorebook(t.getLorebook().stream()
                        .map(lorebookDTOToEntityTranslator::apply)
                        .toList())
                .build();
    }
}
