package me.moirai.storyengine.core.port.outbound.message;

import java.util.List;
import java.util.UUID;

import me.moirai.storyengine.common.dto.MessageSummary;

public interface MessageReader {

    List<MessageSummary> getAllActiveByAdventureId(UUID adventurePublicId);
}
