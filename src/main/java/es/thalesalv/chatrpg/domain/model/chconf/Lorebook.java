package es.thalesalv.chatrpg.domain.model.chconf;

import java.util.Collections;
import java.util.List;
import java.util.Set;

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
public class Lorebook {

    private String id;
    private String name;
    private String description;
    private String owner;
    private List<String> writePermissions;
    private List<String> readPermissions;
    private String visibility;
    private Set<LorebookEntry> entries;

    public static Lorebook defaultLorebook() {

        return Lorebook.builder()
                .id("0")
                .name("DEFAULT LOREBOOK")
                .visibility("private")
                .entries(Collections.emptySet())
                .build();
    }
}