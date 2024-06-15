package es.thalesalv.chatrpg.core.application.usecase.channelconfig.result;

import java.time.OffsetDateTime;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class UpdateChannelConfigResult {

    private final OffsetDateTime lastUpdatedDateTime;

    public static UpdateChannelConfigResult build(OffsetDateTime lastUpdatedDateTime) {

        return new UpdateChannelConfigResult(lastUpdatedDateTime);
    }
}
