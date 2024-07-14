package me.moirai.discordbot.core.domain.persona;

import me.moirai.discordbot.core.domain.CompletionRole;

public class NudgeFixture {

    public static Nudge.Builder sample() {

        return Nudge.builder()
                .content("This is a nudge")
                .role(CompletionRole.fromString("system"));
    }
}
