package es.thalesalv.chatrpg.core.application.usecase.channelconfig.request;

import es.thalesalv.chatrpg.common.usecases.UseCase;

public final class DeleteChannelConfig extends UseCase<Void> {

    private final String id;
    private final String requesterDiscordId;

    private DeleteChannelConfig(String id, String requesterDiscordId) {
        this.id = id;
        this.requesterDiscordId = requesterDiscordId;
    }

    public static DeleteChannelConfig build(String id, String requesterDiscordId) {

        return new DeleteChannelConfig(id, requesterDiscordId);
    }

    public String getId() {
        return id;
    }

    public String getRequesterDiscordId() {
        return requesterDiscordId;
    }
}
