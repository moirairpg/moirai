package es.thalesalv.chatrpg.core.application.query.persona;

import es.thalesalv.chatrpg.core.domain.persona.Persona;
import es.thalesalv.chatrpg.core.domain.persona.PersonaFixture;

public class GetPersonaResultFixture {

    public static GetPersonaResult.Builder privatePersona() {

        Persona persona = PersonaFixture.privatePersona().build();
        return GetPersonaResult.builder()
                .id(persona.getId())
                .name(persona.getName())
                .personality(persona.getPersonality())
                .visibility(persona.getVisibility().name())
                .gameMode(persona.getGameMode().name())
                .readerUsers(persona.getReaderUsers())
                .writerUsers(persona.getWriterUsers())
                .creationDate(persona.getCreationDate())
                .lastUpdateDate(persona.getLastUpdateDate())
                .ownerDiscordId(persona.getOwnerDiscordId())
                .nudgeContent(persona.getNudge().getContent())
                .nudgeRole(persona.getNudge().getRole().toString())
                .bumpContent(persona.getBump().getContent())
                .bumpRole(persona.getBump().getRole().toString())
                .bumpFrequency(persona.getBump().getFrequency());
    }
}
