package me.moirai.storyengine.core.port.outbound.message;

import static me.moirai.storyengine.common.enums.TranscriptChange.DICE_ROLLED;
import static me.moirai.storyengine.common.enums.TranscriptChange.IMPOSSIBLE_ACTION_ATTEMPTED;
import static me.moirai.storyengine.common.enums.TranscriptChange.LEVEL_UP;
import static me.moirai.storyengine.common.enums.TranscriptChange.MESSAGES_REMOVED_AFTER;
import static me.moirai.storyengine.common.enums.TranscriptChange.MESSAGES_REMOVED_FROM;
import static me.moirai.storyengine.common.enums.TranscriptChange.MESSAGE_ADDED;
import static me.moirai.storyengine.common.enums.TranscriptChange.MESSAGE_EDITED;
import static me.moirai.storyengine.common.enums.TranscriptChange.MESSAGE_REMOVED;
import static me.moirai.storyengine.common.enums.TranscriptChange.NARRATION_FAILED;
import static me.moirai.storyengine.common.enums.TranscriptChange.XP_GAINED;

import java.util.UUID;

import me.moirai.storyengine.common.dto.DiceRollSummary;
import me.moirai.storyengine.common.dto.ImpossibleActionSummary;
import me.moirai.storyengine.common.dto.LevelUpSummary;
import me.moirai.storyengine.common.dto.MessageSummary;
import me.moirai.storyengine.common.dto.XpGainSummary;
import me.moirai.storyengine.common.enums.TranscriptChange;

public record AdventureMessageUpdate(
        TranscriptChange change,
        UUID messageId,
        MessageSummary message,
        DiceRollSummary roll,
        ImpossibleActionSummary impossibleAction,
        XpGainSummary xpGain,
        LevelUpSummary levelUp,
        boolean isNarrationPending) {

    public static AdventureMessageUpdate messageAdded(MessageSummary message, boolean isNarrationPending) {
        return new AdventureMessageUpdate(MESSAGE_ADDED, message.id(), message, null, null, null, null,
                isNarrationPending);
    }

    public static AdventureMessageUpdate messageEdited(MessageSummary message, boolean isNarrationPending) {
        return new AdventureMessageUpdate(MESSAGE_EDITED, message.id(), message, null, null, null, null,
                isNarrationPending);
    }

    public static AdventureMessageUpdate messageRemoved(UUID messageId, boolean isNarrationPending) {
        return new AdventureMessageUpdate(MESSAGE_REMOVED, messageId, null, null, null, null, null,
                isNarrationPending);
    }

    public static AdventureMessageUpdate messagesRemovedFrom(UUID messageId, boolean isNarrationPending) {
        return new AdventureMessageUpdate(MESSAGES_REMOVED_FROM, messageId, null, null, null, null, null,
                isNarrationPending);
    }

    public static AdventureMessageUpdate messagesRemovedAfter(UUID messageId, boolean isNarrationPending) {
        return new AdventureMessageUpdate(MESSAGES_REMOVED_AFTER, messageId, null, null, null, null, null,
                isNarrationPending);
    }

    public static AdventureMessageUpdate narrationFailed() {
        return new AdventureMessageUpdate(NARRATION_FAILED, null, null, null, null, null, null, false);
    }

    public static AdventureMessageUpdate diceRolled(UUID playerMessageId, DiceRollSummary roll) {
        return new AdventureMessageUpdate(DICE_ROLLED, playerMessageId, null, roll, null, null, null, true);
    }

    public static AdventureMessageUpdate impossibleActionAttempted(
            UUID playerMessageId,
            ImpossibleActionSummary impossibleAction) {

        return new AdventureMessageUpdate(IMPOSSIBLE_ACTION_ATTEMPTED, playerMessageId, null, null, impossibleAction,
                null, null, true);
    }

    public static AdventureMessageUpdate xpGained(UUID playerMessageId, XpGainSummary xpGain) {
        return new AdventureMessageUpdate(XP_GAINED, playerMessageId, null, null, null, xpGain, null, true);
    }

    public static AdventureMessageUpdate leveledUp(UUID playerMessageId, LevelUpSummary levelUp) {
        return new AdventureMessageUpdate(LEVEL_UP, playerMessageId, null, null, null, null, levelUp, true);
    }
}
