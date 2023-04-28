package es.thalesalv.chatrpg.domain.model.chconf;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class World {

    private String id;
    private String name;
    private String description;
    private String owner;
    private String visibility;
    private List<String> writePermissions;
    private List<String> readPermissions;
    private String initialPrompt;
    private Lorebook lorebook;

    public static World defaultWorld() {

        return World.builder()
                .id("0")
                .name("DEFAULT WORLD")
                .lorebook(Lorebook.defaultLorebook())
                .build();
    }
}