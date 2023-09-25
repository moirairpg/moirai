package es.thalesalv.chatrpg.application.mapper.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.entity.WorldEntity;
import es.thalesalv.chatrpg.application.mapper.lorebook.LorebookEntryEntityToDTO;
import es.thalesalv.chatrpg.domain.model.bot.LorebookEntry;
import es.thalesalv.chatrpg.domain.model.bot.World;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WorldEntityToDTO implements Function<WorldEntity, World> {

    private final LorebookEntryEntityToDTO lorebookEntryEntityToDTO;

    @Override
    public World apply(WorldEntity worldEntity) {

        final List<LorebookEntry> entries = Optional.ofNullable(worldEntity.getLorebook())
                .orElse(new ArrayList<>())
                .stream()
                .map(lorebookEntryEntityToDTO)
                .collect(Collectors.toList());

        return World.builder()
                .id(worldEntity.getId())
                .initialPrompt(worldEntity.getInitialPrompt())
                .name(worldEntity.getName())
                .ownerDiscordId(worldEntity.getOwnerDiscordId())
                .visibility(worldEntity.getVisibility())
                .writePermissions(Optional.ofNullable(worldEntity.getWritePermissions())
                        .orElse(new ArrayList<String>()))
                .readPermissions(Optional.ofNullable(worldEntity.getReadPermissions())
                        .orElse(new ArrayList<String>()))
                .description(worldEntity.getDescription())
                .lorebook(entries)
                .build();
    }
}