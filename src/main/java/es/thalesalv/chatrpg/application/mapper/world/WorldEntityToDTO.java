package es.thalesalv.chatrpg.application.mapper.world;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.entity.WorldEntity;
import es.thalesalv.chatrpg.application.mapper.lorebook.LorebookEntityToDTO;
import es.thalesalv.chatrpg.domain.model.chconf.World;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WorldEntityToDTO implements Function<WorldEntity, World> {

    private final LorebookEntityToDTO lorebookEntityToDTO;

    @Override
    public World apply(WorldEntity worldEntity) {

        return World.builder()
                .id(worldEntity.getId())
                .initialPrompt(worldEntity.getInitialPrompt())
                .name(worldEntity.getName())
                .owner(worldEntity.getOwner())
                .visibility(worldEntity.getVisibility())
                .writePermissions(Optional.ofNullable(worldEntity.getWritePermissions())
                        .orElse(new ArrayList<String>()))
                .readPermissions(Optional.ofNullable(worldEntity.getReadPermissions())
                        .orElse(new ArrayList<String>()))
                .description(worldEntity.getDescription())
                .lorebook(lorebookEntityToDTO.apply(worldEntity.getLorebook()))
                .build();
    }
}