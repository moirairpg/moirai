package es.thalesalv.chatrpg.application.mapper.world;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.entity.WorldEntity;
import es.thalesalv.chatrpg.application.mapper.lorebook.LorebookDTOToEntity;
import es.thalesalv.chatrpg.domain.model.chconf.Lorebook;
import es.thalesalv.chatrpg.domain.model.chconf.World;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WorldDTOToEntity implements Function<World, WorldEntity> {

    private final LorebookDTOToEntity lorebookDTOToEntity;

    @Override
    public WorldEntity apply(World world) {

        final Lorebook lorebook = Optional.ofNullable(world.getLorebook())
                .orElse(Lorebook.defaultLorebook());

        return WorldEntity.builder()
                .id(world.getId())
                .initialPrompt(world.getInitialPrompt())
                .name(world.getName())
                .owner(world.getOwner())
                .visibility(world.getVisibility())
                .writePermissions(Optional.ofNullable(world.getWritePermissions())
                        .orElse(new ArrayList<String>()))
                .readPermissions(Optional.ofNullable(world.getReadPermissions())
                        .orElse(new ArrayList<String>()))
                .description(world.getDescription())
                .lorebook(lorebookDTOToEntity.apply(lorebook))
                .build();
    }
}