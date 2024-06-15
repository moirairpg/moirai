package es.thalesalv.chatrpg.core.application.usecase.world.request;

import es.thalesalv.chatrpg.common.usecases.UseCase;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(builderClassName = "Builder")
public final class DeleteWorldLorebookEntry extends UseCase<Void> {

    private final String lorebookEntryId;
    private final String worldId;
    private final String requesterDiscordId;
}
