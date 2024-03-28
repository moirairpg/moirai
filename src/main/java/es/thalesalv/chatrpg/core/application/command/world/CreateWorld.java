package es.thalesalv.chatrpg.core.application.command.world;

import java.util.List;

import es.thalesalv.chatrpg.common.usecases.UseCase;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(builderClassName = "Builder")
public final class CreateWorld extends UseCase<CreateWorldResult> {

    private final String name;
    private final String description;
    private final String adventureStart;
    private final String visibility;
    private final List<CreateWorldLorebookEntry> lorebookEntries;
    private final List<String> writerUsers;
    private final List<String> readerUsers;
    private final String requesterDiscordId;
}
