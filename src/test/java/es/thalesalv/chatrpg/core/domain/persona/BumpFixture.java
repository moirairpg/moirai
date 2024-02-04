package es.thalesalv.chatrpg.core.domain.persona;

public class BumpFixture {

    public static Bump.Builder sample() {

        return Bump.builder()
                .content("This is a bump")
                .role("system")
                .frequency(5);
    }
}
