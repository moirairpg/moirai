package es.thalesalv.chatrpg.core.application.command.channelconfig;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class CreateChannelConfigResult {

    private final String id;

    public static CreateChannelConfigResult with(String id) {

        return new CreateChannelConfigResult(id);
    }
}
