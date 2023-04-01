package es.thalesalv.chatrpg.adapters.rest.controller;

import java.util.List;
import java.util.stream.Collectors;

import es.thalesalv.chatrpg.adapters.data.repository.PersonaRepository;
import es.thalesalv.chatrpg.application.mapper.chconfig.PersonaEntityToDTO;
import es.thalesalv.chatrpg.domain.model.chconf.Persona;
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
@RequestMapping("/api/v1/persona")
public class PersonaController {

    private final PersonaRepository personaRepository;
    private final PersonaEntityToDTO personaEntityToDTO;

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonaController.class);

    @GetMapping
    public Mono<List<Persona>> getAllPersonas() {

        LOGGER.debug("Received request for listing all personas");
        return Mono.just(personaRepository.findAll())
                .map(personas -> personas.stream()
                        .map(personaEntityToDTO)
                        .collect(Collectors.toList()));
    }

    @GetMapping("{persona-id}")
    public Mono<Persona> getPersonaById(@PathVariable(value = "persona-id") final String personaId) {

        LOGGER.debug("Received request for retrieving persona with id {}", personaId);
        return Mono.just(personaRepository.findById(personaId))
                .map(persona -> persona.map(personaEntityToDTO)
                        .orElseThrow(() -> new RuntimeException()));
    }
}
