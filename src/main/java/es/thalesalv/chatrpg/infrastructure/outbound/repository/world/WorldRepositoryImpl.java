package es.thalesalv.chatrpg.infrastructure.outbound.repository.world;

import org.springframework.stereotype.Repository;

import es.thalesalv.chatrpg.core.domain.world.World;
import es.thalesalv.chatrpg.core.domain.world.WorldRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class WorldRepositoryImpl implements WorldRepository {

    @Override
    public World save(World world) {

        return world;
    }
}
