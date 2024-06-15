package es.thalesalv.chatrpg.core.application.usecase.world.request;

import es.thalesalv.chatrpg.common.usecases.UseCase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DeleteWorld extends UseCase<Void> {

    private final String id;
    private final String requesterDiscordId;

    public static DeleteWorld build(String id, String requesterDiscordId) {

        return new DeleteWorld(id, requesterDiscordId);
    }
}
