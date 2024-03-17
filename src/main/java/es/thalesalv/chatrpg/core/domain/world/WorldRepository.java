package es.thalesalv.chatrpg.core.domain.world;

import java.util.Optional;

import es.thalesalv.chatrpg.core.application.query.world.SearchWorldsWithReadAccess;
import es.thalesalv.chatrpg.core.application.query.world.SearchWorldsResult;
import es.thalesalv.chatrpg.core.application.query.world.SearchWorldsWithWriteAccess;

public interface WorldRepository {

    World save(World world);

    Optional<World> findById(String id, String requesterDiscordId);

    void deleteById(String id);

    SearchWorldsResult searchWorldsWithReadAccess(SearchWorldsWithReadAccess query, String requesterDiscordId);

    SearchWorldsResult searchWorldsWithWriteAccess(SearchWorldsWithWriteAccess query, String requesterDiscordId);
}
