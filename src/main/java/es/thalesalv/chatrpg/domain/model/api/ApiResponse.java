package es.thalesalv.chatrpg.domain.model.api;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import es.thalesalv.chatrpg.domain.model.chconf.Channel;
import es.thalesalv.chatrpg.domain.model.chconf.ChannelConfig;
import es.thalesalv.chatrpg.domain.model.chconf.Lorebook;
import es.thalesalv.chatrpg.domain.model.chconf.LorebookEntry;
import es.thalesalv.chatrpg.domain.model.chconf.ModelSettings;
import es.thalesalv.chatrpg.domain.model.chconf.ModerationSettings;
import es.thalesalv.chatrpg.domain.model.chconf.Persona;
import es.thalesalv.chatrpg.domain.model.chconf.World;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse {

    private List<Channel> channels;
    private List<ChannelConfig> channelConfigs;
    private List<Persona> personas;
    private List<World> worlds;
    private List<Lorebook> lorebooks;
    private List<LorebookEntry> lorebookEntries;
    private List<ModerationSettings> moderationSettingsList;
    private List<ModelSettings> modelSettingsList;
    private Channel channel;
    private ChannelConfig channelConfig;
    private Persona persona;
    private World world;
    private Lorebook lorebook;
    private LorebookEntry lorebookEntry;
    private ModerationSettings moderationSettings;
    private ModelSettings modelSettings;
    private ApiErrorResponse error;

    public static ApiResponse empty() {

        return ApiResponse.builder()
                .build();
    }
}
