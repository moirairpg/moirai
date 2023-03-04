package es.thalesalv.gptbot.domain.model.openai.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LorebookDTO {

    private UUID loreEntryId;
    private String loreEntryName;
    private String loreEntryRegex;
    private String loreEntryDescription;
    private String playerDiscordId;
}
