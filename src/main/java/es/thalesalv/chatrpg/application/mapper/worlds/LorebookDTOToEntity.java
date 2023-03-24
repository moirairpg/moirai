package es.thalesalv.chatrpg.application.mapper.worlds;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.db.entity.LorebookEntity;
import es.thalesalv.chatrpg.adapters.data.db.entity.LorebookRegexEntity;
import es.thalesalv.chatrpg.domain.model.openai.dto.Lorebook;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LorebookDTOToEntity implements Function<Lorebook, LorebookEntity> {

    private final LorebookEntryDTOToEntity lorebookEntryDTOToEntity;

    @Override
    public LorebookEntity apply(Lorebook t) {

        final List<LorebookRegexEntity> entries = t.getEntries().stream()
                .map(lorebookEntryDTOToEntity::apply).collect(Collectors.toList());

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
