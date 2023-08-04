package es.thalesalv.chatrpg.domain.model.api;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import es.thalesalv.chatrpg.domain.model.bot.Channel;
import es.thalesalv.chatrpg.domain.model.bot.ChannelConfig;
import es.thalesalv.chatrpg.domain.model.bot.LorebookEntry;
import es.thalesalv.chatrpg.domain.model.bot.ModelSettings;
import es.thalesalv.chatrpg.domain.model.bot.ModerationSettings;
import es.thalesalv.chatrpg.domain.model.bot.Persona;
import es.thalesalv.chatrpg.domain.model.bot.World;
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
    private List<LorebookEntry> lorebookEntries;
    private List<ModerationSettings> moderationSettingsList;
    private List<ModelSettings> modelSettingsList;
    private Channel channel;
    private ChannelConfig channelConfig;
    private Persona persona;
    private World world;
    private LorebookEntry lorebookEntry;
    private ModerationSettings moderationSettings;
    private ModelSettings modelSettings;
    private ApiErrorResponse error;

    public static ApiResponse empty() {

        return ApiResponse.builder()
                .build();
    }
}
