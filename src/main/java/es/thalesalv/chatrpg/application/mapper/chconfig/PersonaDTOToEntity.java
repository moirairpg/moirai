package es.thalesalv.chatrpg.application.mapper.chconfig;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.entity.BumpEntity;
import es.thalesalv.chatrpg.adapters.data.entity.NudgeEntity;
import es.thalesalv.chatrpg.adapters.data.entity.PersonaEntity;
import es.thalesalv.chatrpg.domain.model.chconf.Persona;

@Component
public class PersonaDTOToEntity implements Function<Persona, PersonaEntity> {

    @Override
    public PersonaEntity apply(Persona t) {

        return PersonaEntity.builder()
                .id(t.getId())
                .name(t.getName())
                .owner(t.getOwner())
                .intent(t.getIntent())
                .personality(t.getPersonality())
                .bump(BumpEntity.builder()
                        .role(t.getBump().getRole())
                        .content(t.getBump().getContent())
                        .frequency(t.getBump().getFrequency())
                        .build())
                .nudge(NudgeEntity.builder()
                        .role(t.getNudge().getRole())
                        .content(t.getNudge().getContent())
                        .build())
                .build();
    }
}
