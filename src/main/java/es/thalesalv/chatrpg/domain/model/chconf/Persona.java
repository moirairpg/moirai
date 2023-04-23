package es.thalesalv.chatrpg.domain.model.chconf;

import es.thalesalv.chatrpg.domain.enums.Intent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Persona {

    private String id;
    private String name;
    private Intent intent;
    private String personality;
    private String owner;
    private String visibility;
    private Nudge nudge;
    private Bump bump;

    public static Persona defaultPersona() {

        return Persona.builder()
                .id("0")
                .name("DEFAULT PERSONA")
                .intent(Intent.CHAT)
                .visibility("private")
                .build();
    }
}
