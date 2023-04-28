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
public class ChannelConfig {

    private String id;
    private String name;
    private String owner;
    private String visibility;
    private List<String> writePermissions;
    private List<String> readPermissions;
    private World world;
    private Persona persona;
    private ModelSettings modelSettings;
    private ModerationSettings moderationSettings;
}
