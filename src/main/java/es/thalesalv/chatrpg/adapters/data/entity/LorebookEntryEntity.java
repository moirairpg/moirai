package es.thalesalv.chatrpg.adapters.data.entity;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "lorebook_entry")
public class LorebookEntryEntity {

    @Id
    @GeneratedValue(generator = "nanoid-generator")
    @GenericGenerator(name = "nanoid-generator", strategy = "es.thalesalv.chatrpg.application.util.dbutils.NanoIdIdentifierGenerator")
    private String id;

    @Column(name = "entry_name", nullable = false)
    private String name;

    @Column(name = "regex", nullable = false)
    private String regex;

    @Column(name = "entry_description", length = 2000, nullable = false)
    private String description;

    @Column(name = "player_discord_id", nullable = true)
    private String playerDiscordId;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "world_id", referencedColumnName = "id", nullable = false)
    private WorldEntity world;
}
