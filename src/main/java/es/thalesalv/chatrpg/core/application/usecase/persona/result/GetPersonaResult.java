package es.thalesalv.chatrpg.core.application.usecase.persona.result;

import static java.util.Collections.unmodifiableList;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public final class GetPersonaResult {

    private final String id;
    private final String name;
    private final String personality;
    private final String nudgeContent;
    private final String nudgeRole;
    private final String bumpContent;
    private final String bumpRole;
    private final int bumpFrequency;
    private final String visibility;
    private final String gameMode;
    private final String ownerDiscordId;
    private final List<String> usersAllowedToWrite;
    private final List<String> usersAllowedToRead;
    private final OffsetDateTime creationDate;
    private final OffsetDateTime lastUpdateDate;

    private GetPersonaResult(Builder builder) {

        this.id = builder.id;
        this.name = builder.name;
        this.personality = builder.personality;
        this.nudgeContent = builder.nudgeContent;
        this.nudgeRole = builder.nudgeRole;
        this.bumpContent = builder.bumpContent;
        this.bumpRole = builder.bumpRole;
        this.bumpFrequency = builder.bumpFrequency;
        this.visibility = builder.visibility;
        this.gameMode = builder.gameMode;
        this.ownerDiscordId = builder.ownerDiscordId;
        this.usersAllowedToWrite = unmodifiableList(builder.usersAllowedToWrite);
        this.usersAllowedToRead = unmodifiableList(builder.usersAllowedToRead);
        this.creationDate = builder.creationDate;
        this.lastUpdateDate = builder.lastUpdateDate;
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

    public String getNudgeContent() {
        return nudgeContent;
    }

    public String getNudgeRole() {
        return nudgeRole;
    }

    public String getBumpContent() {
        return bumpContent;
    }

    public String getBumpRole() {
        return bumpRole;
    }

    public int getBumpFrequency() {
        return bumpFrequency;
    }

    public String getVisibility() {
        return visibility;
    }

    public String getGameMode() {
        return gameMode;
    }

    public String getOwnerDiscordId() {
        return ownerDiscordId;
    }

    public List<String> getUsersAllowedToWrite() {
        return usersAllowedToWrite;
    }

    public List<String> getUsersAllowedToRead() {
        return usersAllowedToRead;
    }

    public OffsetDateTime getCreationDate() {
        return creationDate;
    }

    public OffsetDateTime getLastUpdateDate() {
        return lastUpdateDate;
    }

    public static final class Builder {

        private String id;
        private String name;
        private String personality;
        private String nudgeContent;
        private String nudgeRole;
        private String bumpContent;
        private String bumpRole;
        private int bumpFrequency;
        private String visibility;
        private String gameMode;
        private String ownerDiscordId;
        private List<String> usersAllowedToWrite = new ArrayList<>();
        private List<String> usersAllowedToRead = new ArrayList<>();
        private OffsetDateTime creationDate;
        private OffsetDateTime lastUpdateDate;

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

        public Builder nudgeContent(String nudgeContent) {
            this.nudgeContent = nudgeContent;
            return this;
        }

        public Builder nudgeRole(String nudgeRole) {
            this.nudgeRole = nudgeRole;
            return this;
        }

        public Builder bumpContent(String bumpContent) {
            this.bumpContent = bumpContent;
            return this;
        }

        public Builder bumpRole(String bumpRole) {
            this.bumpRole = bumpRole;
            return this;
        }

        public Builder bumpFrequency(int bumpFrequency) {
            this.bumpFrequency = bumpFrequency;
            return this;
        }

        public Builder visibility(String visibility) {
            this.visibility = visibility;
            return this;
        }

        public Builder gameMode(String gameMode) {
            this.gameMode = gameMode;
            return this;
        }

        public Builder ownerDiscordId(String ownerDiscordId) {
            this.ownerDiscordId = ownerDiscordId;
            return this;
        }

        public Builder usersAllowedToWrite(List<String> usersAllowedToWrite) {

            if (usersAllowedToWrite != null) {
                this.usersAllowedToWrite = usersAllowedToWrite;
            }

            return this;
        }

        public Builder usersAllowedToRead(List<String> usersAllowedToRead) {

            if (usersAllowedToRead != null) {
                this.usersAllowedToRead = usersAllowedToRead;
            }

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

        public GetPersonaResult build() {
            return new GetPersonaResult(this);
        }
    }
}