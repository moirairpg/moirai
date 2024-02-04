package es.thalesalv.chatrpg.core.domain.persona;

public class NudgeFixture {

    public static Nudge.Builder sample() {

        return Nudge.builder()
                .content("This is a nudge")
                .role("system");
    }
}
