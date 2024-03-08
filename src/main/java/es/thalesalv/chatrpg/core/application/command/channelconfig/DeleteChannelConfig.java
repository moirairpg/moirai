package es.thalesalv.chatrpg.core.application.command.channelconfig;

import es.thalesalv.chatrpg.common.cqrs.command.Command;
import lombok.Getter;

@Getter
public final class DeleteChannelConfig extends Command<Void> {

    private final String id;

    private DeleteChannelConfig(String id) {

        this.id = id;
    }

    public static DeleteChannelConfig build(String id) {

        return new DeleteChannelConfig(id);
    }
}
