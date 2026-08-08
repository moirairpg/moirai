package me.moirai.storyengine.core.port.outbound.message;

import me.moirai.storyengine.common.dto.CursorResult;
import me.moirai.storyengine.common.dto.MessageSummary;
import me.moirai.storyengine.core.port.inbound.adventure.SearchAdventureMessages;

public interface MessageSearchReader {

    CursorResult<MessageSummary> search(SearchAdventureMessages query);
}
