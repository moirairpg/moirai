package es.thalesalv.chatrpg.core.application.usecase.world.request;

import es.thalesalv.chatrpg.common.usecases.UseCase;
import es.thalesalv.chatrpg.core.application.usecase.world.result.GetWorldResult;

public final class GetWorldById extends UseCase<GetWorldResult> {

    private final String id;
    private final String requesterDiscordId;

    public GetWorldById(String id, String requesterDiscordId) {
        this.id = id;
        this.requesterDiscordId = requesterDiscordId;
    }

    public static GetWorldById build(String id, String requesterDiscordId) {

        return new GetWorldById(id, requesterDiscordId);
    }

    public String getId() {
        return id;
    }

    public String getRequesterDiscordId() {
        return requesterDiscordId;
    }
}
