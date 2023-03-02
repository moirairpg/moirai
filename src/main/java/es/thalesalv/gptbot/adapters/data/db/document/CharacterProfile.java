package es.thalesalv.gptbot.adapters.data.db.document;

import org.bson.types.ObjectId;
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
    private ObjectId id;
    private String playerDiscordId;
    private String name;
    private String description;
}
