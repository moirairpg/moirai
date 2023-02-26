package es.thalesalv.gptbot.data.db.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "character_profile")
public class CharacterProfileEntity {
    
    @Id
    @Column(name = "id", unique = true, nullable = false)
    private UUID id;
    
    @Column(name = "player_discord_id", unique = true, nullable = true)
    private String playerDiscordId;

    @Column(name = "character_name")
    private String name;

    @Column(name = "character_description", length = 1000)
    private String description;
}
