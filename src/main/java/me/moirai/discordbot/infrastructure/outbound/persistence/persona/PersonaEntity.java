package me.moirai.discordbot.infrastructure.outbound.persistence.persona;

import java.time.OffsetDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import me.moirai.discordbot.common.annotation.NanoId;
import me.moirai.discordbot.infrastructure.outbound.persistence.ShareableAssetEntity;

@Entity(name = "Persona")
@Table(name = "persona")
public class PersonaEntity extends ShareableAssetEntity {

    @Id
    @NanoId
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "personality", nullable = false)
    private String personality;

    private PersonaEntity(Builder builder) {

        super(builder.creatorDiscordId, builder.creationDate,
                builder.lastUpdateDate, builder.ownerDiscordId, builder.usersAllowedToRead, builder.usersAllowedToWrite,
                builder.visibility, builder.version);

        this.id = builder.id;
        this.name = builder.name;
        this.personality = builder.personality;
    }

    protected PersonaEntity() {
        super();
    }

    public static Builder builder() {

        return new Builder();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPersonality() {
        return personality;
    }

    public static final class Builder {

        private String id;
        private String name;
        private String personality;
        private String ownerDiscordId;
        private List<String> usersAllowedToRead;
        private List<String> usersAllowedToWrite;
        private String visibility;
        private String creatorDiscordId;
        private OffsetDateTime creationDate;
        private OffsetDateTime lastUpdateDate;
        private int version;

        private Builder() {
        }

        public Builder id(String id) {

            this.id = id;
            return this;
        }

        public Builder name(String name) {

            this.name = name;
            return this;
        }

        public Builder personality(String personality) {

            this.personality = personality;
            return this;
        }

        public Builder visibility(String visibility) {

            this.visibility = visibility;
            return this;
        }

        public Builder ownerDiscordId(String ownerDiscordId) {

            this.ownerDiscordId = ownerDiscordId;
            return this;
        }

        public Builder usersAllowedToRead(List<String> usersAllowedToRead) {

            this.usersAllowedToRead = usersAllowedToRead;
            return this;
        }

        public Builder usersAllowedToWrite(List<String> usersAllowedToWrite) {

            this.usersAllowedToWrite = usersAllowedToWrite;
            return this;
        }

        public Builder creatorDiscordId(String creatorDiscordId) {

            this.creatorDiscordId = creatorDiscordId;
            return this;
        }

        public Builder creationDate(OffsetDateTime creationDate) {

            this.creationDate = creationDate;
            return this;
        }

        public Builder lastUpdateDate(OffsetDateTime lastUpdateDate) {

            this.lastUpdateDate = lastUpdateDate;
            return this;
        }

        public Builder version(int version) {

            this.version = version;
            return this;
        }

        public PersonaEntity build() {

            return new PersonaEntity(this);
        }
    }
}
