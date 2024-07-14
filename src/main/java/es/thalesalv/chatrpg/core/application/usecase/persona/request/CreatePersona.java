package es.thalesalv.chatrpg.core.application.usecase.persona.request;

import java.util.List;

import es.thalesalv.chatrpg.common.usecases.UseCase;
import es.thalesalv.chatrpg.core.application.usecase.persona.result.CreatePersonaResult;
import reactor.core.publisher.Mono;

public final class CreatePersona extends UseCase<Mono<CreatePersonaResult>> {

    private final String name;
    private final String personality;
    private final String nudgeRole;
    private final String nudgeContent;
    private final String bumpRole;
    private final String bumpContent;
    private final String visibility;
    private final String gameMode;
    private final Integer bumpFrequency;
    private final List<String> usersAllowedToWrite;
    private final List<String> usersAllowedToRead;
    private final String requesterDiscordId;

    public CreatePersona(Builder builder) {
        this.name = builder.name;
        this.personality = builder.personality;
        this.nudgeRole = builder.nudgeRole;
        this.nudgeContent = builder.nudgeContent;
        this.bumpRole = builder.bumpRole;
        this.bumpContent = builder.bumpContent;
        this.visibility = builder.visibility;
        this.gameMode = builder.gameMode;
        this.bumpFrequency = builder.bumpFrequency;
        this.usersAllowedToWrite = builder.usersAllowedToWrite;
        this.usersAllowedToRead = builder.usersAllowedToRead;
        this.requesterDiscordId = builder.requesterDiscordId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public String getPersonality() {
        return personality;
    }

    public String getNudgeRole() {
        return nudgeRole;
    }

    public String getNudgeContent() {
        return nudgeContent;
    }

    public String getBumpRole() {
        return bumpRole;
    }

    public String getBumpContent() {
        return bumpContent;
    }

    public String getVisibility() {
        return visibility;
    }

    public String getGameMode() {
        return gameMode;
    }

    public Integer getBumpFrequency() {
        return bumpFrequency;
    }

    public List<String> getUsersAllowedToWrite() {
        return usersAllowedToWrite;
    }

    public List<String> getUsersAllowedToRead() {
        return usersAllowedToRead;
    }

    public String getRequesterDiscordId() {
        return requesterDiscordId;
    }

    public static final class Builder {

        private String name;
        private String personality;
        private String nudgeRole;
        private String nudgeContent;
        private String bumpRole;
        private String bumpContent;
        private String visibility;
        private String gameMode;
        private Integer bumpFrequency;
        private List<String> usersAllowedToWrite;
        private List<String> usersAllowedToRead;
        private String requesterDiscordId;

        private Builder() {
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder personality(String personality) {
            this.personality = personality;
            return this;
        }

        public Builder nudgeRole(String nudgeRole) {
            this.nudgeRole = nudgeRole;
            return this;
        }

        public Builder nudgeContent(String nudgeContent) {
            this.nudgeContent = nudgeContent;
            return this;
        }

        public Builder bumpRole(String bumpRole) {
            this.bumpRole = bumpRole;
            return this;
        }

        public Builder bumpContent(String bumpContent) {
            this.bumpContent = bumpContent;
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

        public Builder bumpFrequency(Integer bumpFrequency) {
            this.bumpFrequency = bumpFrequency;
            return this;
        }

        public Builder usersAllowedToWrite(List<String> usersAllowedToWrite) {
            this.usersAllowedToWrite = usersAllowedToWrite;
            return this;
        }

        public Builder usersAllowedToRead(List<String> usersAllowedToRead) {
            this.usersAllowedToRead = usersAllowedToRead;
            return this;
        }

        public Builder requesterDiscordId(String requesterDiscordId) {
            this.requesterDiscordId = requesterDiscordId;
            return this;
        }

        public CreatePersona build() {
            return new CreatePersona(this);
        }
    }
}
