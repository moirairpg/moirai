package es.thalesalv.chatrpg.core.application.command.channelconfig;

import es.thalesalv.chatrpg.common.usecases.UseCase;
import lombok.Getter;

@Getter
public final class DeleteChannelConfig extends UseCase<Void> {

    private final String id;

    private DeleteChannelConfig(String id) {

        this.id = id;
    }

    public static DeleteChannelConfig build(String id) {

        return new DeleteChannelConfig(id);
    }
}
