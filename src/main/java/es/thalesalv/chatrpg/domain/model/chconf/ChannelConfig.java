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
public class ChannelConfig {

    private String id;
    private String owner;
    private String editPermissions;
    private World world;
    private Persona persona;
    private Settings settings;

    public static ChannelConfig defaultChannelConfig() {

        return ChannelConfig.builder()
                .id("0")
                .build();
    }
}
