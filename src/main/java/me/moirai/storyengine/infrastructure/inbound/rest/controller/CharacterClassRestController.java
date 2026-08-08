package me.moirai.storyengine.infrastructure.inbound.rest.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import me.moirai.storyengine.common.cqs.query.QueryRunner;
import me.moirai.storyengine.common.web.SecurityContextAware;
import me.moirai.storyengine.core.port.inbound.character.CharacterClassResult;
import me.moirai.storyengine.core.port.inbound.character.GetCharacterClasses;

@RestController
@RequestMapping("/character-classes")
@Tag(name = "Character Classes", description = "Endpoints for retrieving selectable player character classes")
public class CharacterClassRestController extends SecurityContextAware {

    private final QueryRunner queryRunner;

    public CharacterClassRestController(QueryRunner queryRunner) {
        this.queryRunner = queryRunner;
    }

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<CharacterClassResult> getCharacterClasses() {

        return queryRunner.run(new GetCharacterClasses());
    }
}
