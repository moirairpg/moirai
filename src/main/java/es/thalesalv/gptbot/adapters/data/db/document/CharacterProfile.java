package es.thalesalv.gptbot.adapters.data.db.document;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Document
@NoArgsConstructor
@AllArgsConstructor
public class CharacterProfile {

    @Id
    private UUID id;
    private String playerDiscordId;
    private String name;
    private String description;
}
