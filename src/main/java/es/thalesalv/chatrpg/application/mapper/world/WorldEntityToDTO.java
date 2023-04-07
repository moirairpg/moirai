package es.thalesalv.chatrpg.application.mapper.world;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.entity.WorldEntity;
import es.thalesalv.chatrpg.application.mapper.lorebook.LorebookEntryEntityToDTO;
import es.thalesalv.chatrpg.domain.model.chconf.Lorebook;
import es.thalesalv.chatrpg.domain.model.chconf.LorebookEntry;
import es.thalesalv.chatrpg.domain.model.chconf.World;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WorldEntityToDTO implements Function<WorldEntity, World> {

    private final LorebookEntryEntityToDTO lorebookEntryToDTO;

    @Override
    public World apply(WorldEntity worldEntity) {

        final Set<LorebookEntry> entries = worldEntity.getLorebook()
                .getEntries()
                .stream()
                .map(lorebookEntryToDTO)
                .collect(Collectors.toSet());

        return World.builder()
                .editPermissions(worldEntity.getEditPermissions())
                .id(worldEntity.getId())
                .initialPrompt(worldEntity.getInitialPrompt())
                .name(worldEntity.getName())
                .owner(worldEntity.getOwner())
                .visibility(worldEntity.getVisibility())
                .description(worldEntity.getDescription())
                .lorebook(Lorebook.builder()
                        .id(worldEntity.getLorebook()
                                .getId())
                        .name(worldEntity.getLorebook()
                                .getName())
                        .owner(worldEntity.getLorebook()
                                .getOwner())
                        .writePermissions(worldEntity.getLorebook()
                                .getWritePermissions())
                        .readPermissions(worldEntity.getLorebook()
                                .getReadPermissions())
                        .description(worldEntity.getLorebook()
                                .getDescription())
                        .entries(entries)
                        .build())
                .build();
    }
}