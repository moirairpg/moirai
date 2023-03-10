package es.thalesalv.chatrpg.adapters.data.db.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "lorebook")
public class LorebookEntry {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    private UUID id;

    @Column(name = "entry_name")
    private String name;

    @Column(name = "entry_description", length = 1000)
    private String description;

    /**
     * ID of the Discord user who "owns" this entry, when it's a PC
     */
    @Column(name = "player_discord_id", unique = true, nullable = true)
    private String playerDiscordId;
}
