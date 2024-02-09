package es.thalesalv.chatrpg.core.domain.world;

import java.util.Optional;

public interface WorldRepository {

    World save(World world);

    Optional<World> findById(String id);
}
