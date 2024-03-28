package es.thalesalv.chatrpg.core.application.command.persona;

import es.thalesalv.chatrpg.common.usecases.UseCase;
import lombok.Getter;

@Getter
public final class DeletePersona extends UseCase<Void> {

    private final String id;
    private final String requesterDiscordId;

    private DeletePersona(String id, String requesterDiscordId) {

        this.id = id;
        this.requesterDiscordId = requesterDiscordId;
    }

    public static DeletePersona build(String id, String requesterDiscordId) {

        return new DeletePersona(id, requesterDiscordId);
    }
}
