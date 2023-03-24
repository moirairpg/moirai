package es.thalesalv.chatrpg.domain.model.openai.dto;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Lorebook {

    private String id;
    private String name;
    private String description;
    private String owner;
    private String editPermissions;
    private String visibility;
    private List<LorebookEntry> entries;
}
