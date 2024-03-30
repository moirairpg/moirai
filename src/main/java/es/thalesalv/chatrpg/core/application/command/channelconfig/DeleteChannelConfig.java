package es.thalesalv.chatrpg.core.application.command.channelconfig;

import es.thalesalv.chatrpg.common.usecases.UseCase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DeleteChannelConfig extends UseCase<Void> {

    private final String id;
    private final String requesterDiscordId;

    public static DeleteChannelConfig build(String id, String requesterDiscordId) {

        return new DeleteChannelConfig(id, requesterDiscordId);
    }
}
