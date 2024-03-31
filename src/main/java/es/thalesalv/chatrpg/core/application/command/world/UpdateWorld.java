package es.thalesalv.chatrpg.core.application.command.world;

import java.util.List;

import es.thalesalv.chatrpg.common.usecases.UseCase;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(builderClassName = "Builder")
public final class UpdateWorld extends UseCase<UpdateWorldResult> {

    private final String id;
    private final String name;
    private final String description;
    private final String adventureStart;
    private final String visibility;
    private final List<String> usersAllowedToWriteToAdd;
    private final List<String> usersAllowedToWriteToRemove;
    private final List<String> usersAllowedToReadToAdd;
    private final List<String> usersAllowedToReadToRemove;
    private final String requesterDiscordId;
}
