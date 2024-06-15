package es.thalesalv.chatrpg.core.application.usecase.channelconfig.request;

import es.thalesalv.chatrpg.common.usecases.UseCase;
import es.thalesalv.chatrpg.core.application.usecase.channelconfig.result.GetChannelConfigResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class GetChannelConfigById extends UseCase<GetChannelConfigResult> {

    private final String id;
    private final String requesterDiscordId;

    public static GetChannelConfigById build(String id, String requesterDiscordId) {

        return new GetChannelConfigById(id, requesterDiscordId);
    }
}
