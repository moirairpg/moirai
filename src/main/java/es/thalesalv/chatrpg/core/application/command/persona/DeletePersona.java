package es.thalesalv.chatrpg.core.application.command.persona;

import es.thalesalv.chatrpg.common.usecases.UseCase;
import lombok.Getter;

@Getter
public final class DeletePersona extends UseCase<Void> {

    private final String id;

    private DeletePersona(String id) {

        this.id = id;
    }

    public static DeletePersona build(String id) {

        return new DeletePersona(id);
    }
}
