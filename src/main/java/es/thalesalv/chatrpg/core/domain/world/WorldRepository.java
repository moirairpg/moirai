package es.thalesalv.chatrpg.core.domain.world;

import java.util.Optional;

import es.thalesalv.chatrpg.core.application.query.world.SearchWorlds;
import es.thalesalv.chatrpg.core.application.query.world.SearchWorldsResult;

public interface WorldRepository {

    World save(World world);

    Optional<World> findById(String id);

    void deleteById(String id);

    SearchWorldsResult searchWorlds(SearchWorlds query);
}
