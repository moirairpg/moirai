package me.moirai.storyengine.core.application.query.adventure;

import me.moirai.storyengine.common.annotation.QueryHandler;
import me.moirai.storyengine.common.cqs.query.AbstractQueryHandler;
import me.moirai.storyengine.common.exception.NotFoundException;
import me.moirai.storyengine.core.port.inbound.adventure.AdventureDetails;
import me.moirai.storyengine.core.port.inbound.adventure.AdventureMembershipSummary;
import me.moirai.storyengine.core.port.inbound.adventure.GetAdventureById;
import me.moirai.storyengine.core.port.outbound.adventure.AdventureReader;
import me.moirai.storyengine.core.port.outbound.adventure.AdventureRosterReader;
import me.moirai.storyengine.core.port.outbound.storage.StoragePort;

@QueryHandler
public class GetAdventureByIdHandler extends AbstractQueryHandler<GetAdventureById, AdventureDetails> {

    private static final String ADVENTURE_NOT_FOUND = "Adventure to be viewed was not found";
    private static final String ID_CANNOT_BE_NULL_OR_EMPTY = "Adventure ID cannot be null or empty";

    private final AdventureReader reader;
    private final AdventureRosterReader adventureRosterReader;
    private final StoragePort storagePort;

    public GetAdventureByIdHandler(
            AdventureReader reader,
            AdventureRosterReader adventureRosterReader,
            StoragePort storagePort) {

        this.reader = reader;
        this.adventureRosterReader = adventureRosterReader;
        this.storagePort = storagePort;
    }

    @Override
    public void validate(GetAdventureById command) {

        if (command.adventureId() == null) {
            throw new IllegalArgumentException(ID_CANNOT_BE_NULL_OR_EMPTY);
        }
    }

    @Override
    public AdventureDetails execute(GetAdventureById query) {

        var adventure = reader.getAdventureById(query.adventureId())
                .orElseThrow(() -> new NotFoundException(ADVENTURE_NOT_FOUND));

        var roster = adventureRosterReader.getAllByAdventurePublicId(query.adventureId()).stream()
                .map(row -> new AdventureMembershipSummary(
                        row.playerCharacterId(),
                        row.playerId(),
                        row.playerUsername(),
                        row.name(),
                        row.characterClass(),
                        storagePort.resolveUrl(row.imageKey())))
                .toList();

        return new AdventureDetails(
                adventure.id(),
                adventure.name(),
                adventure.description(),
                adventure.adventureStart(),
                adventure.worldId(),
                adventure.narratorName(),
                adventure.narratorPersonality(),
                adventure.visibility(),
                adventure.moderation(),
                storagePort.resolveUrl(adventure.imageKey()),
                adventure.creationDate(),
                adventure.lastUpdateDate(),
                adventure.modelConfiguration(),
                adventure.contextAttributes(),
                adventure.permissions(),
                adventure.lorebook(),
                roster,
                adventure.uiImagePositionX(),
                adventure.uiImagePositionY());
    }
}
