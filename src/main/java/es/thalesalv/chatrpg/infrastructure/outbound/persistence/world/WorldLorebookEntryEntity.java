package es.thalesalv.chatrpg.infrastructure.outbound.persistence.world;

import org.hibernate.annotations.GenericGenerator;

import es.thalesalv.chatrpg.infrastructure.outbound.persistence.AssetEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "WorldLorebookEntry")
@Table(name = "WorldLorebookEntry")
public class WorldLorebookEntryEntity extends AssetEntity {

    @Id
    @GeneratedValue(generator = "nanoid-generator")
    @GenericGenerator(name = "nanoid-generator", strategy = "es.thalesalv.chatrpg.common.dbutil.NanoIdIdentifierGenerator")
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "regex")
    private String regex;

    @Column(name = "player_discord_id")
    private String playerDiscordId;

    @Column(name = "is_player_character", nullable = false)
    private boolean isPlayerCharacter;

    @Column(name = "world_id", nullable = false)
    private String worldId;

    public WorldLorebookEntryEntity(Builder builder) {

        super();

        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.regex = builder.regex;
        this.isPlayerCharacter = builder.isPlayerCharacter;
        this.worldId = builder.worldId;
        this.playerDiscordId = builder.playerDiscordId;
    }

    protected WorldLorebookEntryEntity() {
        super();
    }

    public static Builder builder() {

        return new Builder();
    }

    public static class Builder {

        private String id;
        private String name;
        private String description;
        private String regex;
        private String playerDiscordId;
        private boolean isPlayerCharacter;
        private String worldId;

        public Builder id(String id) {

            this.id = id;
            return this;
        }

        public Builder name(String name) {

            this.name = name;
            return this;
        }

        public Builder description(String description) {

            this.description = description;
            return this;
        }

        public Builder regex(String regex) {

            this.regex = regex;
            return this;
        }

        public Builder playerDiscordId(String playerDiscordId) {

            this.playerDiscordId = playerDiscordId;
            return this;
        }

        public Builder isPlayerCharacter(boolean isPlayerCharacter) {

            this.isPlayerCharacter = isPlayerCharacter;
            return this;
        }

        public Builder worldId(String worldId) {

            this.worldId = worldId;
            return this;
        }

        public WorldLorebookEntryEntity build() {

            return new WorldLorebookEntryEntity(this);
        }
    }
}
