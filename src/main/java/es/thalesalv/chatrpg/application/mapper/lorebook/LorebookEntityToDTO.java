package es.thalesalv.chatrpg.application.mapper.lorebook;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntity;
import es.thalesalv.chatrpg.domain.model.chconf.Lorebook;
import es.thalesalv.chatrpg.domain.model.chconf.LorebookEntry;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LorebookEntityToDTO implements Function<LorebookEntity, Lorebook> {

    private final LorebookEntryEntityToDTO lorebookEntryEntityToDTO;

    @Override
    public Lorebook apply(LorebookEntity t) {

        final Set<LorebookEntry> entries = t.getEntries()
                .stream()
                .map(lorebookEntryEntityToDTO)
                .collect(Collectors.toSet());
        return Lorebook.builder()
                .id(t.getId())
                .description(t.getDescription())
                .name(t.getName())
                .editPermissions(t.getEditPermissions())
                .owner(t.getOwner())
                .visibility(t.getVisibility())
                .entries(entries)
                .build();
    }
}