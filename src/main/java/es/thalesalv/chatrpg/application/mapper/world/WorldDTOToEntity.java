package es.thalesalv.chatrpg.application.mapper.world;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntity;
import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntryRegexEntity;
import es.thalesalv.chatrpg.adapters.data.entity.WorldEntity;
import es.thalesalv.chatrpg.application.mapper.lorebook.LorebookEntryDTOToEntity;
import es.thalesalv.chatrpg.domain.model.chconf.Lorebook;
import es.thalesalv.chatrpg.domain.model.chconf.World;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WorldDTOToEntity implements Function<World, WorldEntity> {

    private final LorebookEntryDTOToEntity lorebookEntryDTOToEntity;

    @Override
    public WorldEntity apply(World world) {

        final Lorebook lorebook = Optional.ofNullable(world.getLorebook())
                .orElse(Lorebook.defaultLorebook());

        final List<LorebookEntryRegexEntity> entries = Optional.ofNullable(lorebook.getEntries())
                .orElse(new HashSet<>())
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
                        .id(lorebook.getId())
                        .name(lorebook.getName())
                        .owner(lorebook.getOwner())
                        .writePermissions(Optional.ofNullable(lorebook.getWritePermissions())
                                .orElse(StringUtils.EMPTY))
                        .readPermissions(Optional.ofNullable(lorebook.getReadPermissions())
                                .orElse(StringUtils.EMPTY))
                        .description(lorebook.getDescription())
                        .visibility(Optional.ofNullable(lorebook.getVisibility())
                                .orElse("private"))
                        .entries(entries)
                        .build())
                .build();
    }
}