package es.thalesalv.chatrpg.application.mapper.world;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntryRegexEntity;
import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntity;
import es.thalesalv.chatrpg.adapters.data.entity.WorldEntity;
import es.thalesalv.chatrpg.application.mapper.lorebook.LorebookEntryDTOToEntity;
import es.thalesalv.chatrpg.domain.model.chconf.World;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WorldDTOToEntity implements Function<World, WorldEntity> {

    private final LorebookEntryDTOToEntity lorebookEntryDTOToEntity;

    @Override
    public WorldEntity apply(World t) {

        final List<LorebookEntryRegexEntity> entries = t.getLorebook().getEntries()
                .stream().map(lorebookEntryDTOToEntity).collect(Collectors.toList());

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