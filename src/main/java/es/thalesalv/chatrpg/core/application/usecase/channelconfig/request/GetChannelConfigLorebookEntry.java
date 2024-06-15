package es.thalesalv.chatrpg.core.application.usecase.channelconfig.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(builderClassName = "Builder")
public final class GetChannelConfigLorebookEntry {

    private String name;
    private String regex;
    private String description;
    private String playerDiscordId;
}
