package es.thalesalv.chatrpg.core.application.command.world;

import es.thalesalv.chatrpg.common.usecases.UseCase;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(builderClassName = "Builder")
public final class CreateWorldLorebookEntry extends UseCase<CreateWorldLorebookEntryResult> {

    private final String worldId;
    private final String name;
    private final String regex;
    private final String description;
    private final String playerDiscordId;
    private final boolean isPlayerCharacter;
    private final String requesterDiscordId;
}
