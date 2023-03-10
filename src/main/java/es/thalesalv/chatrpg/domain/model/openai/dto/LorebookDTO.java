package es.thalesalv.chatrpg.domain.model.openai.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LorebookDTO {

    private UUID id;
    private String name;
    private String regex;
    private String description;
    private String playerDiscordId;
}
