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
    private final List<String> writerUsersToAdd;
    private final List<String> writerUsersToRemove;
    private final List<String> readerUsersToAdd;
    private final List<String> readerUsersToRemove;
    private final String requesterDiscordId;
}
