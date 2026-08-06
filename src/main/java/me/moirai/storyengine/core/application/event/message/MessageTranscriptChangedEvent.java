package me.moirai.storyengine.core.application.event.message;

import java.util.UUID;

import me.moirai.storyengine.core.port.outbound.message.AdventureMessageUpdate;

public record MessageTranscriptChangedEvent(UUID adventurePublicId, AdventureMessageUpdate update) {
}
