package es.thalesalv.chatrpg.infrastructure.outbound.persistence;

import java.time.OffsetDateTime;
import java.util.List;

import org.hibernate.annotations.Formula;

import es.thalesalv.chatrpg.common.dbutil.StringListConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class ShareableAssetEntity extends AssetEntity {

    protected ShareableAssetEntity(String creatorDiscordId, OffsetDateTime creationDate,
            OffsetDateTime lastUpdateDate, String ownerDiscordId, List<String> usersAllowedToRead,
            List<String> usersAllowedToWrite, String visibility) {

        super(creatorDiscordId, creationDate, lastUpdateDate);

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

    @Column(name = "discord_users_allowed_to_read")
    @Convert(converter = StringListConverter.class)
    protected List<String> usersAllowedToRead;

    @Formula(value = "discord_users_allowed_to_read")
    private String usersAllowedToReadString;

    @Column(name = "discord_users_allowed_to_write")
    @Convert(converter = StringListConverter.class)
    protected List<String> usersAllowedToWrite;

    @Formula(value = "discord_users_allowed_to_write")
    private String usersAllowedToWriteString;

    @Column(name = "visibility")
    protected String visibility;
}
