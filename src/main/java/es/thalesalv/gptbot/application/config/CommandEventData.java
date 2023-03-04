package es.thalesalv.gptbot.application.config;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CommandEventData {

    private UUID lorebookEntryId;
    private UUID lorebookEntryRegexId;
    private String discordUserId;
}
