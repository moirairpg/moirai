package es.thalesalv.chatrpg.application.mapper.chconfig;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.db.entity.BumpEntity;
import es.thalesalv.chatrpg.adapters.data.db.entity.NudgeEntity;
import es.thalesalv.chatrpg.adapters.data.db.entity.PersonaEntity;
import es.thalesalv.chatrpg.domain.model.openai.dto.Persona;

@Component
public class PersonaDTOToEntity implements Function<Persona, PersonaEntity> {

    @Override
    public PersonaEntity apply(Persona t) {

        return PersonaEntity.builder()
                .id(t.getId())
                .intent(t.getIntent())
                .name(t.getName())
                .owner(t.getOwner())
                .personality(t.getPersonality())
                .nudge(NudgeEntity.builder()
                        .content(t.getNudge().getContent())
                        .role(t.getNudge().getRole())
                        .build())
                .bump(BumpEntity.builder()
                        .content(t.getBump().getContent())
                        .frequency(t.getBump().getFrequency())
                        .role(t.getBump().getRole())
                        .build())
                .build();
    }
}
