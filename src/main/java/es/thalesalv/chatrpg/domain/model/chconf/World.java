package es.thalesalv.chatrpg.domain.model.chconf;

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
    private String description;
    private String owner;
    private String editPermissions;
    private String visibility;
    private String initialPrompt;
    private Lorebook lorebook;

    public static World defaultWorld() {

        return World.builder()
                .id("0")
                .build();
    }
}