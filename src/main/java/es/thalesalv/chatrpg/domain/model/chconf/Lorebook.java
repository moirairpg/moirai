package es.thalesalv.chatrpg.domain.model.chconf;

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
public class Lorebook {

    private String id;
    private String name;
    private String description;
    private String owner;
    private String editPermissions;
    private String visibility;
    private Set<LorebookEntry> entries;

    public static Lorebook defaultLorebook() {

        return Lorebook.builder()
                .id("0")
                .build();
    }
}