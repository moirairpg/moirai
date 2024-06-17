package es.thalesalv.chatrpg.core.domain.world;

import java.util.List;

import es.thalesalv.chatrpg.core.application.usecase.world.request.CreateWorld;
import es.thalesalv.chatrpg.core.application.usecase.world.request.CreateWorldLorebookEntry;
import es.thalesalv.chatrpg.core.application.usecase.world.request.DeleteWorld;
import es.thalesalv.chatrpg.core.application.usecase.world.request.DeleteWorldLorebookEntry;
import es.thalesalv.chatrpg.core.application.usecase.world.request.GetWorldById;
import es.thalesalv.chatrpg.core.application.usecase.world.request.GetWorldLorebookEntryById;
import es.thalesalv.chatrpg.core.application.usecase.world.request.UpdateWorld;
import es.thalesalv.chatrpg.core.application.usecase.world.request.UpdateWorldLorebookEntry;
import reactor.core.publisher.Mono;

public interface WorldService {

    World getWorldById(GetWorldById query);

    Mono<World> createFrom(CreateWorld command);

    Mono<World> update(UpdateWorld command);

    void deleteWorld(DeleteWorld command);

    WorldLorebookEntry createLorebookEntry(CreateWorldLorebookEntry command);

    WorldLorebookEntry updateLorebookEntry(UpdateWorldLorebookEntry command);

    List<WorldLorebookEntry> findAllEntriesByRegex(String requesterDiscordId, String worldId, String valueToSearch);

    List<WorldLorebookEntry> findAllEntriesByRegex(String worldId, String valueToSearch);

    WorldLorebookEntry findWorldLorebookEntryById(GetWorldLorebookEntryById query);

    void deleteLorebookEntry(DeleteWorldLorebookEntry command);
}
