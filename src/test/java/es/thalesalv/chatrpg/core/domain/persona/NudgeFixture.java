package es.thalesalv.chatrpg.core.domain.persona;

import es.thalesalv.chatrpg.core.domain.CompletionRole;

public class NudgeFixture {

    public static Nudge.Builder sample() {

        return Nudge.builder()
                .content("This is a nudge")
                .role(CompletionRole.fromString("system"));
    }
}
