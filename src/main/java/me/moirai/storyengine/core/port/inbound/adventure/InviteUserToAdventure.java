package me.moirai.storyengine.core.port.inbound.adventure;

import java.util.List;
import java.util.UUID;

import me.moirai.storyengine.common.cqs.command.Command;
import me.moirai.storyengine.common.util.Functions;

public record InviteUserToAdventure(
        UUID adventureId,
        List<String> inviteeUsernames)
        implements Command<InviteUserToAdventureResult> {

    public InviteUserToAdventure {
        inviteeUsernames = Functions.mapOrDefault(inviteeUsernames, List.of(), List::copyOf);
    }
}
