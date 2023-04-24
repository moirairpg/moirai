package es.thalesalv.chatrpg.application.mapper.world;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
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
                .id(worldEntity.getId())
                .initialPrompt(worldEntity.getInitialPrompt())
                .name(worldEntity.getName())
                .owner(worldEntity.getOwner())
                .visibility(worldEntity.getVisibility())
                .visibility(worldEntity.getVisibility())
                .readPermissions(worldEntity.getReadPermissions())
                .description(worldEntity.getDescription())
                .lorebook(Lorebook.builder()
                        .id(worldEntity.getLorebook()
                                .getId())
                        .name(worldEntity.getLorebook()
                                .getName())
                        .owner(worldEntity.getLorebook()
                                .getOwner())
                        .writePermissions(Optional.ofNullable(worldEntity.getLorebook()
                                .getWritePermissions())
                                .orElse(StringUtils.EMPTY))
                        .readPermissions(Optional.ofNullable(worldEntity.getLorebook()
                                .getReadPermissions())
                                .orElse(StringUtils.EMPTY))
                        .description(worldEntity.getLorebook()
                                .getDescription())
                        .visibility(Optional.ofNullable(worldEntity.getLorebook()
                                .getVisibility())
                                .orElse("private"))
                        .entries(entries)
                        .build())
                .build();
    }
}