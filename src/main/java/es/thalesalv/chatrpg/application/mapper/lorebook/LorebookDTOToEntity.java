package es.thalesalv.chatrpg.application.mapper.lorebook;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntity;
import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntryRegexEntity;
import es.thalesalv.chatrpg.domain.model.chconf.Lorebook;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LorebookDTOToEntity implements Function<Lorebook, LorebookEntity> {

    private final LorebookEntryDTOToEntity lorebookEntryDTOToEntity;

    @Override
    public LorebookEntity apply(Lorebook t) {

        final List<LorebookEntryRegexEntity> entries = t.getEntries()
                .stream()
                .map(lorebookEntryDTOToEntity)
                .collect(Collectors.toList());

        return LorebookEntity.builder()
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