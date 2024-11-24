package me.moirai.discordbot.infrastructure.outbound.persistence;

import java.time.OffsetDateTime;
import java.util.List;

import org.hibernate.annotations.Formula;

import me.moirai.discordbot.common.dbutil.StringListConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class ShareableAssetEntity extends AssetEntity {

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

    protected ShareableAssetEntity(String creatorDiscordId, OffsetDateTime creationDate,
            OffsetDateTime lastUpdateDate, String ownerDiscordId, List<String> usersAllowedToRead,
            List<String> usersAllowedToWrite, String visibility, int version) {

        super(creatorDiscordId, creationDate, lastUpdateDate, version);

        this.ownerDiscordId = ownerDiscordId;
        this.usersAllowedToRead = usersAllowedToRead;
        this.usersAllowedToWrite = usersAllowedToWrite;
        this.visibility = visibility;
    }

    protected ShareableAssetEntity() {
        super();
    }

    public String getOwnerDiscordId() {
        return ownerDiscordId;
    }

    public List<String> getUsersAllowedToRead() {
        return usersAllowedToRead;
    }

    public String getUsersAllowedToReadString() {
        return usersAllowedToReadString;
    }

    public List<String> getUsersAllowedToWrite() {
        return usersAllowedToWrite;
    }

    public String getUsersAllowedToWriteString() {
        return usersAllowedToWriteString;
    }

    public String getVisibility() {
        return visibility;
    }
}
