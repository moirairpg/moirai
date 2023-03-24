package es.thalesalv.chatrpg.application.mapper.worlds;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.db.entity.LorebookEntity;
import es.thalesalv.chatrpg.adapters.data.db.entity.LorebookRegexEntity;
import es.thalesalv.chatrpg.adapters.data.db.entity.WorldEntity;
import es.thalesalv.chatrpg.domain.model.openai.dto.World;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WorldDTOToEntity implements Function<World, WorldEntity> {

    private final LorebookEntryDTOToEntity lorebookEntryDTOToEntity;

    @Override
    public WorldEntity apply(World t) {

        final List<LorebookRegexEntity> entries = t.getLorebook().getEntries()
                .stream().map(lorebookEntryDTOToEntity::apply).collect(Collectors.toList());

        return WorldEntity.builder()
                .editPermissions(t.getEditPermissions())
                .id(t.getId())
                .initialPrompt(t.getInitialPrompt())
                .name(t.getName())
                .owner(t.getOwner())
                .visibility(t.getVisibility())
                .description(t.getDescription())
                .lorebook(LorebookEntity.builder()
                        .id(t.getLorebook().getId())
                        .name(t.getLorebook().getName())
                        .owner(t.getLorebook().getOwner())
                        .editPermissions(t.getLorebook().getEditPermissions())
                        .description(t.getLorebook().getDescription())
                        .entries(entries)
                        .build())
                .build();
    }
}
