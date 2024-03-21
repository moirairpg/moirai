package es.thalesalv.chatrpg.core.application.command.world;

import es.thalesalv.chatrpg.common.usecases.UseCase;
import lombok.Getter;

@Getter
public final class DeleteWorldLorebookEntry extends UseCase<Void> {

    private final String id;

    private DeleteWorldLorebookEntry(String id) {

        this.id = id;
    }

    public static DeleteWorldLorebookEntry build(String id) {

        return new DeleteWorldLorebookEntry(id);
    }
}
