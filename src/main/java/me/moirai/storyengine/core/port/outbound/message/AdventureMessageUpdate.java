package me.moirai.storyengine.core.port.outbound.message;

import static me.moirai.storyengine.common.enums.TranscriptChange.MESSAGES_REMOVED_FROM;
import static me.moirai.storyengine.common.enums.TranscriptChange.MESSAGE_ADDED;
import static me.moirai.storyengine.common.enums.TranscriptChange.MESSAGE_EDITED;
import static me.moirai.storyengine.common.enums.TranscriptChange.MESSAGE_REMOVED;
import static me.moirai.storyengine.common.enums.TranscriptChange.NARRATION_FAILED;

import java.util.UUID;

import me.moirai.storyengine.common.enums.TranscriptChange;
import me.moirai.storyengine.core.port.inbound.message.MessageResult;

public record AdventureMessageUpdate(
        TranscriptChange change,
        UUID messageId,
        MessageResult message,
        boolean isNarrationPending) {

    public static AdventureMessageUpdate messageAdded(MessageResult message, boolean isNarrationPending) {
        return new AdventureMessageUpdate(MESSAGE_ADDED, message.id(), message, isNarrationPending);
    }

    public static AdventureMessageUpdate messageEdited(MessageResult message, boolean isNarrationPending) {
        return new AdventureMessageUpdate(MESSAGE_EDITED, message.id(), message, isNarrationPending);
    }

    public static AdventureMessageUpdate messageRemoved(UUID messageId, boolean isNarrationPending) {
        return new AdventureMessageUpdate(MESSAGE_REMOVED, messageId, null, isNarrationPending);
    }

    public static AdventureMessageUpdate messagesRemovedFrom(UUID messageId, boolean isNarrationPending) {
        return new AdventureMessageUpdate(MESSAGES_REMOVED_FROM, messageId, null, isNarrationPending);
    }

    public static AdventureMessageUpdate narrationFailed() {
        return new AdventureMessageUpdate(NARRATION_FAILED, null, null, false);
    }
}
