package es.thalesalv.chatrpg.core.domain.persona;

import es.thalesalv.chatrpg.core.domain.CompletionRole;

public class BumpFixture {

    public static Bump.Builder sample() {

        return Bump.builder()
                .content("This is a bump")
                .role(CompletionRole.fromString("system"))
                .frequency(5);
    }
}
