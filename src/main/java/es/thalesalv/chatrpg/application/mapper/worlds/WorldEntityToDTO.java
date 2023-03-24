package es.thalesalv.chatrpg.application.mapper.worlds;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.db.entity.WorldEntity;
import es.thalesalv.chatrpg.domain.model.openai.dto.Lorebook;
import es.thalesalv.chatrpg.domain.model.openai.dto.LorebookEntry;
import es.thalesalv.chatrpg.domain.model.openai.dto.World;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WorldEntityToDTO implements Function<WorldEntity, World> {

    private final LorebookEntryToDTO lorebookEntryToDTO;

    @Override
    public World apply(WorldEntity t) {

        final List<LorebookEntry> entries = t.getLorebook().getEntries()
                .stream().map(lorebookEntryToDTO).collect(Collectors.toList());

        return World.builder()
                .editPermissions(t.getEditPermissions())
                .id(t.getId())
                .initialPrompt(t.getInitialPrompt())
                .name(t.getName())
                .owner(t.getOwner())
                .visibility(t.getVisibility())
                .description(t.getDescription())
                .lorebook(Lorebook.builder()
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
