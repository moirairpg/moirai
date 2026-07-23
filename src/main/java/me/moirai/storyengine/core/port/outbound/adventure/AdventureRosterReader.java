package me.moirai.storyengine.core.port.outbound.adventure;

import java.util.List;
import java.util.UUID;

public interface AdventureRosterReader {

    List<CharacterAdventureSummaryRow> getAdventuresByPlayerCharacterPublicId(UUID characterPublicId);

    List<AdventureRosterSummaryRow> getAllByAdventurePublicId(UUID adventurePublicId);
}
