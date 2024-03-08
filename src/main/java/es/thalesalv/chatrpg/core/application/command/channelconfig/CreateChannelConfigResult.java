package es.thalesalv.chatrpg.core.application.command.channelconfig;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class CreateChannelConfigResult {

    private final String id;

    public static CreateChannelConfigResult build(String id) {

        return new CreateChannelConfigResult(id);
    }
}
