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
    public Lorebook apply(LorebookEntity lorebookEntity) {

        final Set<LorebookEntry> entries = lorebookEntity.getEntries()
                .stream()
                .map(lorebookEntryEntityToDTO)
                .collect(Collectors.toSet());

        return Lorebook.builder()
                .id(lorebookEntity.getId())
                .description(lorebookEntity.getDescription())
                .name(lorebookEntity.getName())
                .writePermissions(lorebookEntity.getWritePermissions())
                .readPermissions(lorebookEntity.getReadPermissions())
                .owner(lorebookEntity.getOwner())
                .visibility(lorebookEntity.getVisibility())
                .entries(entries)
                .build();
    }
}