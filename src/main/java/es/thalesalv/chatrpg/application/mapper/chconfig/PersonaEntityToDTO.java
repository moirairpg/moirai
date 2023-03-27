package es.thalesalv.chatrpg.application.mapper.chconfig;

import java.util.Optional;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.entity.BumpEntity;
import es.thalesalv.chatrpg.adapters.data.entity.NudgeEntity;
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
                .bump(buildBump(t.getBump()))
                .nudge(buildNudge(t.getNudge()))
                .build();
    }

    private Nudge buildNudge(NudgeEntity nudge) {

        return Optional.ofNullable(nudge)
                .map(n -> Nudge.builder()
                        .role(n.getRole())
                        .content(n.getContent())
                        .build())
                .orElse(Nudge.builder()
                        .role(StringUtils.EMPTY)
                        .content(StringUtils.EMPTY)
                        .build());
    }

    private Bump buildBump(BumpEntity bump) {

        return Optional.ofNullable(bump)
                .map(b -> Bump.builder()
                        .role(b.getRole())
                        .content(b.getContent())
                        .frequency(b.getFrequency())
                        .build())
                .orElse(Bump.builder()
                        .content(StringUtils.EMPTY)
                        .role(StringUtils.EMPTY)
                        .frequency(0)
                        .build());
    }
}
