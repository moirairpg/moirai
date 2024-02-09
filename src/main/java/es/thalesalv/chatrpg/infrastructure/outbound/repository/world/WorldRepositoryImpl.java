package es.thalesalv.chatrpg.infrastructure.outbound.repository.world;

import java.util.Optional;

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

    @Override
    public Optional<World> findById(String id) {

        return Optional.of(World.builder().build());
    }
}
