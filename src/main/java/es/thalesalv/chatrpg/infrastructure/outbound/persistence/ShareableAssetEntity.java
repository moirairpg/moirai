package es.thalesalv.chatrpg.infrastructure.outbound.persistence;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class ShareableAssetEntity extends AssetEntity {

    protected ShareableAssetEntity(String ownerDiscordId, List<String> usersAllowedToRead,
            List<String> usersAllowedToWrite, String visibility) {

        super(ownerDiscordId);

        this.ownerDiscordId = ownerDiscordId;
        this.usersAllowedToRead = usersAllowedToRead;
        this.usersAllowedToWrite = usersAllowedToWrite;
        this.visibility = visibility;
    }

    protected ShareableAssetEntity() {
        super();
    }

    @Column(name = "owner_discordId")
    protected String ownerDiscordId;

    @Column(name = "reader_users_ids")
    protected List<String> usersAllowedToRead;

    @Column(name = "writers_users_ids")
    protected List<String> usersAllowedToWrite;

    @Column(name = "visibility")
    protected String visibility;
}
