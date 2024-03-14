package es.thalesalv.chatrpg.infrastructure.outbound.persistence;

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
    @Convert(converter = StringListConverter.class)
    protected List<String> usersAllowedToRead;

    @Formula(value = "reader_users_ids")
    private String usersAllowedToReadString;

    @Column(name = "writers_users_ids")
    @Convert(converter = StringListConverter.class)
    protected List<String> usersAllowedToWrite;

    @Formula(value = "writers_users_ids")
    private String usersAllowedToWriteString;

    @Column(name = "visibility")
    protected String visibility;
}
