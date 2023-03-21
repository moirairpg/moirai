package es.thalesalv.chatrpg.domain.model.openai.dto;

import java.util.Set;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class World {

    private String id;
    private String name;
    private String owner;
    private String editPermissions;
    private String visibility;
    private String initialPrompt;
    private Set<LorebookEntry> lorebook;
}
