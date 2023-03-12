package es.thalesalv.chatrpg.application.config;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;

@Getter
@Setter
@Builder
public class CommandEventData {

    private Message messageToBeEdited;
    private UUID lorebookEntryId;
    private UUID lorebookEntryRegexId;
    private SelfUser discordUser;
    private Persona persona;
}
