package es.thalesalv.chatrpg.domain.model.openai.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LorebookEntry {

    private String id;
    private String name;
    private String regex;
    private String regexId;
    private String description;
    private String playerDiscordId;
}
