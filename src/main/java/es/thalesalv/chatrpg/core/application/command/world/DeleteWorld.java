package es.thalesalv.chatrpg.core.application.command.world;

import es.thalesalv.chatrpg.common.cqrs.command.Command;
import lombok.Getter;

@Getter
public final class DeleteWorld extends Command<Void> {

    private final String id;

    private DeleteWorld(String id) {

        this.id = id;
    }

    public static DeleteWorld build(String id) {

        return new DeleteWorld(id);
    }
}
