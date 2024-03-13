package es.thalesalv.chatrpg.infrastructure.outbound.persistence.world;

import java.util.List;

import org.hibernate.annotations.GenericGenerator;

import es.thalesalv.chatrpg.infrastructure.outbound.persistence.ShareableAssetEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "world")
public class WorldEntity extends ShareableAssetEntity {

    @Id
    @GeneratedValue(generator = "nanoid-generator")
    @GenericGenerator(name = "nanoid-generator", strategy = "es.thalesalv.chatrpg.common.dbutil.NanoIdIdentifierGenerator")
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "adventure_start", nullable = false)
    private String adventureStart;

    public WorldEntity(Builder builder) {

        super(builder.ownerDiscordId, builder.usersAllowedToRead, builder.usersAllowedToWrite, builder.visibility);

        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.adventureStart = builder.adventureStart;
    }

    protected WorldEntity() {
        super();
    }

    public static Builder builder() {

        return new Builder();
    }

    public static class Builder {

        private String id;
        private String name;
        private String adventureStart;
        private String description;
        private String ownerDiscordId;
        private List<String> usersAllowedToRead;
        private List<String> usersAllowedToWrite;
        private String visibility;

        public Builder id(String id) {

            this.id = id;
            return this;
        }

        public Builder name(String name) {

            this.name = name;
            return this;
        }

        public Builder adventureStart(String adventureStart) {

            this.adventureStart = adventureStart;
            return this;
        }

        public Builder description(String description) {

            this.description = description;
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

        public WorldEntity build() {

            return new WorldEntity(this);
        }
    }
}
