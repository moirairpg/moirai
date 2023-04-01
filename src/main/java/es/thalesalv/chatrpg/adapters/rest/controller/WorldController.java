package es.thalesalv.chatrpg.adapters.rest.controller;

import java.util.List;
import java.util.stream.Collectors;

import es.thalesalv.chatrpg.adapters.data.repository.WorldRepository;
import es.thalesalv.chatrpg.application.mapper.world.WorldEntityToDTO;
import es.thalesalv.chatrpg.domain.model.chconf.World;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/world")
public class WorldController {

    private final WorldEntityToDTO worldEntityToDTO;
    private final WorldRepository worldRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(WorldController.class);

    @GetMapping
    public Mono<List<World>> getAllWorlds() {

        LOGGER.debug("Received request for listing all worlds");
        return Mono.just(worldRepository.findAll())
                .map(worlds -> worlds.stream()
                        .map(worldEntityToDTO)
                        .collect(Collectors.toList()));
    }

    @GetMapping("{id}")
    public Mono<World> getWorldById(@PathVariable(value = "id") String worldId) {

        LOGGER.debug("Received request for retrieving world with id {}", worldId);
        return Mono.just(worldRepository.findById(worldId))
                .map(world -> world.map(worldEntityToDTO)
                        .orElseThrow(() -> new RuntimeException()));
    }
}
