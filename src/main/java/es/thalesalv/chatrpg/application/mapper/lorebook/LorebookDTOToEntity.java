package es.thalesalv.chatrpg.application.mapper.lorebook;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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
                .writePermissions(Optional.ofNullable(lorebook.getWritePermissions())
                        .orElse(new ArrayList<String>()))
                .readPermissions(Optional.ofNullable(lorebook.getReadPermissions())
                        .orElse(new ArrayList<String>()))
                .owner(lorebook.getOwner())
                .visibility(Optional.ofNullable(lorebook.getVisibility())
                        .orElse("private"))
                .entries(entries)
                .build();
    }
}