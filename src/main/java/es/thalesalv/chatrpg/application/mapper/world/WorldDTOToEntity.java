package es.thalesalv.chatrpg.application.mapper.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntryEntity;
import es.thalesalv.chatrpg.adapters.data.entity.WorldEntity;
import es.thalesalv.chatrpg.application.mapper.lorebook.LorebookEntryDTOToEntity;
import es.thalesalv.chatrpg.domain.model.bot.World;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WorldDTOToEntity implements Function<World, WorldEntity> {

    private final LorebookEntryDTOToEntity lorebookEntryDTOToEntity;

    @Override
    public WorldEntity apply(World world) {

        final WorldEntity worldEntity = WorldEntity.builder()
                .id(world.getId())
                .initialPrompt(world.getInitialPrompt())
                .name(world.getName())
                .ownerDiscordId(world.getOwnerDiscordId())
                .visibility(world.getVisibility())
                .writePermissions(Optional.ofNullable(world.getWritePermissions())
                        .orElse(new ArrayList<String>()))
                .readPermissions(Optional.ofNullable(world.getReadPermissions())
                        .orElse(new ArrayList<String>()))
                .description(world.getDescription())
                .build();

        final List<LorebookEntryEntity> lorebook = Optional.ofNullable(world.getLorebook())
                .orElse(new ArrayList<>())
                .stream()
                .map(lorebookEntryDTOToEntity)
                .map(e -> {
                    e.setWorld(worldEntity);
                    return e;
                })
                .collect(Collectors.toList());

        worldEntity.setLorebook(lorebook);
        return worldEntity;
    }
}