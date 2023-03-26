package es.thalesalv.chatrpg.domain.model.chconf;

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
    private String intent;
    private String personality;
    private String owner;
    private Nudge nudge;
    private Bump bump;
}
