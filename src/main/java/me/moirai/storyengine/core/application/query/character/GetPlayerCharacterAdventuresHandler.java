package me.moirai.storyengine.core.application.query.character;

import java.util.List;

import me.moirai.storyengine.common.annotation.QueryHandler;
import me.moirai.storyengine.common.cqs.query.AbstractQueryHandler;
import me.moirai.storyengine.core.port.inbound.adventure.CharacterAdventureSummary;
import me.moirai.storyengine.core.port.inbound.character.GetPlayerCharacterAdventures;
import me.moirai.storyengine.core.port.outbound.adventure.AdventureRosterReader;
import me.moirai.storyengine.core.port.outbound.storage.StoragePort;

@QueryHandler
public class GetPlayerCharacterAdventuresHandler
        extends AbstractQueryHandler<GetPlayerCharacterAdventures, List<CharacterAdventureSummary>> {

    private final AdventureRosterReader adventureRosterReader;
    private final StoragePort storagePort;

    public GetPlayerCharacterAdventuresHandler(
            AdventureRosterReader adventureRosterReader,
            StoragePort storagePort) {

        this.adventureRosterReader = adventureRosterReader;
        this.storagePort = storagePort;
    }

    @Override
    public List<CharacterAdventureSummary> execute(GetPlayerCharacterAdventures query) {

        return adventureRosterReader.getAdventuresByPlayerCharacterPublicId(query.characterId()).stream()
                .map(adventure -> new CharacterAdventureSummary(
                        adventure.publicId(),
                        adventure.name(),
                        storagePort.resolveUrl(adventure.imageKey()),
                        adventure.uiImagePositionX(),
                        adventure.uiImagePositionY()))
                .toList();
    }
}
