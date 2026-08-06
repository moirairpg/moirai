package me.moirai.storyengine.core.application.event.message;

import java.util.UUID;

public record MessageSentEvent(UUID adventurePublicId) {
}
