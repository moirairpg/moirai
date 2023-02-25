package es.thalesalv.gptbot.model.bot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChannelSettings {

    @JsonProperty("channel_ids")
    private List<String> channelIds;

    @JsonProperty("channel_purpose")
    private String channelPurpose;

    @JsonProperty("channel_instructions")
    private String channelInstructions;

    @JsonProperty("max_tokens")
    private int maxTokens;

    @JsonProperty("temperature")
    private double temperature;

    @JsonProperty("personality")
    private BotPersonality personality;
}
