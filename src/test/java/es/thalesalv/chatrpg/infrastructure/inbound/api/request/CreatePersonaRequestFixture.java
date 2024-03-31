package es.thalesalv.chatrpg.infrastructure.inbound.api.request;

import es.thalesalv.chatrpg.core.domain.persona.Persona;
import es.thalesalv.chatrpg.core.domain.persona.PersonaFixture;

public class CreatePersonaRequestFixture {

    public static CreatePersonaRequest.Builder createPrivatePersona() {

        Persona persona = PersonaFixture.privatePersona().build();
        return CreatePersonaRequest.builder()
                .name(persona.getName())
                .personality(persona.getPersonality())
                .visibility(persona.getVisibility().toString())
                .gameMode(persona.getGameMode().name())
                .readerUsers(persona.getReaderUsers())
                .writerUsers(persona.getWriterUsers())
                .nudgeContent(persona.getNudge().getContent())
                .nudgeRole(persona.getNudge().getRole().toString())
                .bumpContent(persona.getBump().getContent())
                .bumpRole(persona.getBump().getRole().toString())
                .bumpFrequency(persona.getBump().getFrequency());
    }
}
