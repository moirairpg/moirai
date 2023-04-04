package es.thalesalv.chatrpg.application.service.api;

import java.util.Arrays;
import java.util.List;

import es.thalesalv.chatrpg.adapters.data.repository.WorldRepository;
import es.thalesalv.chatrpg.application.mapper.world.WorldDTOToEntity;
import es.thalesalv.chatrpg.application.mapper.world.WorldEntityToDTO;
import es.thalesalv.chatrpg.domain.exception.WorldNotFoundException;
import es.thalesalv.chatrpg.domain.model.chconf.World;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class WorldService {

    private final WorldDTOToEntity worldDTOToEntity;
    private final WorldEntityToDTO worldEntityToDTO;

    private final WorldRepository worldRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(WorldService.class);

    public Mono<List<World>> retrieveAllWorlds() {

        LOGGER.debug("Retrieving world data from request");
        return Mono.just(worldRepository.findAll())
                .map(worlds -> worlds.stream()
                        .map(worldEntityToDTO)
                        .toList());
    }

    public Mono<List<World>> retrieveWorldById(final String worldId) {

        LOGGER.debug("Retrieving world by ID data from request");
        return Mono.just(worldRepository.findById(worldId)
                .orElseThrow(WorldNotFoundException::new))
                .map(worldEntityToDTO)
                .map(Arrays::asList);
    }

    public Mono<List<World>> saveWorld(final World world) {

        LOGGER.debug("Saving world data from request");
        return Mono.just(worldDTOToEntity.apply(world))
                .map(worldRepository::save)
                .map(worldEntityToDTO)
                .map(Arrays::asList);
    }

    public Mono<List<World>> updateWorld(final String worldId, final World world) {

        LOGGER.debug("Updating world data from request");
        return Mono.just(worldDTOToEntity.apply(world))
                .map(c -> {
                    c.setId(worldId);
                    return worldRepository.save(c);
                })
                .map(worldEntityToDTO)
                .map(Arrays::asList);
    }

    public void deleteWorld(final String worldId) {

        LOGGER.debug("Deleting world data from request");
        worldRepository.deleteById(worldId);
    }
}
