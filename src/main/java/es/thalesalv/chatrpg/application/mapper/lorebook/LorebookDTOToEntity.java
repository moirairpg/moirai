package es.thalesalv.chatrpg.application.mapper.lorebook;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntity;
import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntryRegexEntity;
import es.thalesalv.chatrpg.domain.model.chconf.Lorebook;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LorebookDTOToEntity implements Function<Lorebook, LorebookEntity> {

    private final LorebookEntryDTOToEntity lorebookEntryDTOToEntity;

    @Override
    public LorebookEntity apply(Lorebook lorebook) {

        final List<LorebookEntryRegexEntity> entries = Optional.ofNullable(lorebook.getEntries())
                .orElse(new HashSet<>())
                .stream()
                .map(lorebookEntryDTOToEntity)
                .collect(Collectors.toList());

        return LorebookEntity.builder()
                .id(lorebook.getId())
                .description(lorebook.getDescription())
                .name(lorebook.getName())
                .writePermissions(lorebook.getWritePermissions())
                .readPermissions(lorebook.getReadPermissions())
                .owner(lorebook.getOwner())
                .visibility(lorebook.getVisibility())
                .entries(entries)
                .build();
    }
}