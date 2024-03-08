package es.thalesalv.chatrpg.core.application.query.channelconfig;

import es.thalesalv.chatrpg.common.usecases.UseCase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class GetChannelConfigById extends UseCase<GetChannelConfigResult> {

    private final String id;

    public static GetChannelConfigById build(String id) {

        return new GetChannelConfigById(id);
    }
}
