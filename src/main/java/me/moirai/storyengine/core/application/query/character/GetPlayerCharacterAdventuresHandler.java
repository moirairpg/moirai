package me.moirai.storyengine.core.application.query.character;

import java.util.List;

import me.moirai.storyengine.common.annotation.QueryHandler;
import me.moirai.storyengine.common.cqs.query.AbstractQueryHandler;
import me.moirai.storyengine.core.port.inbound.adventure.CharacterAdventureSummary;
import me.moirai.storyengine.core.port.inbound.character.GetPlayerCharacterAdventures;
import me.moirai.storyengine.core.port.outbound.adventure.AdventureRosterReader;

@QueryHandler
public class GetPlayerCharacterAdventuresHandler
        extends AbstractQueryHandler<GetPlayerCharacterAdventures, List<CharacterAdventureSummary>> {

    private final AdventureRosterReader adventureRosterReader;

    public GetPlayerCharacterAdventuresHandler(AdventureRosterReader adventureRosterReader) {
        this.adventureRosterReader = adventureRosterReader;
    }

    @Override
    public List<CharacterAdventureSummary> execute(GetPlayerCharacterAdventures query) {

        return adventureRosterReader.getAdventuresByPlayerCharacterPublicId(query.characterId()).stream()
                .map(adventure -> new CharacterAdventureSummary(adventure.publicId(), adventure.name()))
                .toList();
    }
}
