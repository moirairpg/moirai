package me.moirai.storyengine.core.application.event.message;

import java.util.UUID;

public record NarrationRetriedFromMessageEvent(UUID adventurePublicId) {
}
