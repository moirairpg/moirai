package me.moirai.storyengine.core.port.outbound.character;

import java.util.List;

import me.moirai.storyengine.common.dto.PaginatedResult;
import me.moirai.storyengine.core.port.inbound.character.SearchPlayerCharacters;

public interface PlayerCharacterSearchReader {

    PaginatedResult<PlayerCharacterSummaryRow> search(SearchPlayerCharacters query);

    List<PlayerCharacterSummaryRow> listByName(String name, Long requesterId);
}