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
    public WorldEntity apply(World world) {

        final List<LorebookEntryRegexEntity> entries = world.getLorebook()
                .getEntries()
                .stream()
                .map(lorebookEntryDTOToEntity)
                .collect(Collectors.toList());

        return WorldEntity.builder()
                .editPermissions(world.getEditPermissions())
                .id(world.getId())
                .initialPrompt(world.getInitialPrompt())
                .name(world.getName())
                .owner(world.getOwner())
                .visibility(world.getVisibility())
                .description(world.getDescription())
                .lorebook(LorebookEntity.builder()
                        .id(world.getLorebook()
                                .getId())
                        .name(world.getLorebook()
                                .getName())
                        .owner(world.getLorebook()
                                .getOwner())
                        .editPermissions(world.getLorebook()
                                .getEditPermissions())
                        .description(world.getLorebook()
                                .getDescription())
                        .entries(entries)
                        .build())
                .build();
    }
}