package es.thalesalv.chatrpg.application.mapper.chconfig;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.db.entity.PersonaEntity;
import es.thalesalv.chatrpg.domain.model.openai.dto.Bump;
import es.thalesalv.chatrpg.domain.model.openai.dto.Nudge;
import es.thalesalv.chatrpg.domain.model.openai.dto.Persona;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PersonaEntityToDTO implements Function<PersonaEntity, Persona> {

    @Override
    public Persona apply(PersonaEntity t) {

        return Persona.builder()
                .id(t.getId())
                .intent(t.getIntent())
                .name(t.getName())
                .owner(t.getOwner())
                .personality(t.getPersonality())
                .nudge(Nudge.builder()
                        .content(t.getNudge().getContent())
                        .role(t.getNudge().getRole())
                        .build())
                .bump(Bump.builder()
                        .content(t.getBump().getContent())
                        .frequency(t.getBump().getFrequency())
                        .role(t.getBump().getRole())
                        .build())
                .build();
    }
}
