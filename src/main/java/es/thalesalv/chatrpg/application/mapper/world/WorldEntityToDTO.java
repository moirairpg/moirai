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
    public World apply(WorldEntity t) {

        final Set<LorebookEntry> entries = t.getLorebook()
                .getEntries()
                .stream()
                .map(lorebookEntryToDTO)
                .collect(Collectors.toSet());
        return World.builder()
                .editPermissions(t.getEditPermissions())
                .id(t.getId())
                .initialPrompt(t.getInitialPrompt())
                .name(t.getName())
                .owner(t.getOwner())
                .visibility(t.getVisibility())
                .description(t.getDescription())
                .lorebook(Lorebook.builder()
                        .id(t.getLorebook()
                                .getId())
                        .name(t.getLorebook()
                                .getName())
                        .owner(t.getLorebook()
                                .getOwner())
                        .editPermissions(t.getLorebook()
                                .getEditPermissions())
                        .description(t.getLorebook()
                                .getDescription())
                        .entries(entries)
                        .build())
                .build();
    }
}