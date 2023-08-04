package es.thalesalv.chatrpg.domain.model.bot;

import java.util.Collections;
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
public class World {

    private String id;
    private String name;
    private String description;
    private String owner;
    private String visibility;
    private String initialPrompt;
    private List<String> writePermissions;
    private List<String> readPermissions;
    private List<LorebookEntry> lorebook;

    public static World defaultWorld() {

        return World.builder()
                .id("0")
                .name("DEFAULT WORLD")
                .lorebook(Collections.emptyList())
                .build();
    }
}