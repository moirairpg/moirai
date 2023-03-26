package es.thalesalv.chatrpg.application.mapper.chconfig;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.entity.PersonaEntity;
import es.thalesalv.chatrpg.domain.model.chconf.Bump;
import es.thalesalv.chatrpg.domain.model.chconf.Nudge;
import es.thalesalv.chatrpg.domain.model.chconf.Persona;

@Component
public class PersonaEntityToDTO implements Function<PersonaEntity, Persona> {

    @Override
    public Persona apply(PersonaEntity t) {

        return Persona.builder()
                .id(t.getId())
                .name(t.getName())
                .owner(t.getOwner())
                .intent(t.getIntent())
                .personality(t.getPersonality())
                .bump(Bump.builder()
                        .role(t.getBump().getRole())
                        .content(t.getBump().getContent())
                        .frequency(t.getBump().getFrequency())
                        .build())
                .nudge(Nudge.builder()
                        .role(t.getNudge().getRole())
                        .content(t.getNudge().getContent())
                        .build())
                .build();
    }
}
