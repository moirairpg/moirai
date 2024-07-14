package me.moirai.discordbot.core.application.usecase.persona.request;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;

import me.moirai.discordbot.common.usecases.UseCase;
import me.moirai.discordbot.core.application.usecase.persona.result.UpdatePersonaResult;
import reactor.core.publisher.Mono;

public final class UpdatePersona extends UseCase<Mono<UpdatePersonaResult>> {

    private final String id;
    private final String name;
    private final String personality;
    private final String nudgeRole;
    private final String nudgeContent;
    private final String bumpRole;
    private final String bumpContent;
    private final String visibility;
    private final String gameMode;
    private final Integer bumpFrequency;
    private final List<String> usersAllowedToWriteToAdd;
    private final List<String> usersAllowedToWriteToRemove;
    private final List<String> usersAllowedToReadToAdd;
    private final List<String> usersAllowedToReadToRemove;
    private final String requesterDiscordId;

    private UpdatePersona(Builder builder) {

        this.id = builder.id;
        this.name = builder.name;
        this.personality = builder.personality;
        this.nudgeRole = builder.nudgeRole;
        this.nudgeContent = builder.nudgeContent;
        this.bumpRole = builder.bumpRole;
        this.bumpContent = builder.bumpContent;
        this.visibility = builder.visibility;
        this.gameMode = builder.gameMode;
        this.bumpFrequency = builder.bumpFrequency;
        this.usersAllowedToWriteToAdd = unmodifiableList(builder.usersAllowedToWriteToAdd);
        this.usersAllowedToWriteToRemove = unmodifiableList(builder.usersAllowedToWriteToRemove);
        this.usersAllowedToReadToAdd = unmodifiableList(builder.usersAllowedToReadToAdd);
        this.usersAllowedToReadToRemove = unmodifiableList(builder.usersAllowedToReadToRemove);
        this.requesterDiscordId = builder.requesterDiscordId;
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

    public List<String> getUsersAllowedToWriteToAdd() {
        return usersAllowedToWriteToAdd;
    }

    public List<String> getUsersAllowedToWriteToRemove() {
        return usersAllowedToWriteToRemove;
    }

    public List<String> getUsersAllowedToReadToAdd() {
        return usersAllowedToReadToAdd;
    }

    public List<String> getUsersAllowedToReadToRemove() {
        return usersAllowedToReadToRemove;
    }

    public String getRequesterDiscordId() {
        return requesterDiscordId;
    }

    public static final class Builder {
        private String id;
        private String name;
        private String personality;
        private String nudgeRole;
        private String nudgeContent;
        private String bumpRole;
        private String bumpContent;
        private String visibility;
        private String gameMode;
        private Integer bumpFrequency;
        private List<String> usersAllowedToWriteToAdd = new ArrayList<>();
        private List<String> usersAllowedToWriteToRemove = new ArrayList<>();
        private List<String> usersAllowedToReadToAdd = new ArrayList<>();
        private List<String> usersAllowedToReadToRemove = new ArrayList<>();
        private String requesterDiscordId;

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

        public Builder usersAllowedToWriteToAdd(List<String> usersAllowedToWriteToAdd) {

            if (usersAllowedToWriteToAdd != null) {
                this.usersAllowedToWriteToAdd = usersAllowedToWriteToAdd;
            }

            return this;
        }

        public Builder usersAllowedToWriteToRemove(List<String> usersAllowedToWriteToRemove) {

            if (usersAllowedToWriteToRemove != null) {
                this.usersAllowedToWriteToRemove = usersAllowedToWriteToRemove;
            }

            return this;
        }

        public Builder usersAllowedToReadToAdd(List<String> usersAllowedToReadToAdd) {

            if (usersAllowedToReadToAdd != null) {
                this.usersAllowedToReadToAdd = usersAllowedToReadToAdd;
            }

            return this;
        }

        public Builder usersAllowedToReadToRemove(List<String> usersAllowedToReadToRemove) {

            if (usersAllowedToReadToRemove != null) {
                this.usersAllowedToReadToRemove = usersAllowedToReadToRemove;
            }

            return this;
        }

        public Builder requesterDiscordId(String requesterDiscordId) {
            this.requesterDiscordId = requesterDiscordId;
            return this;
        }

        public UpdatePersona build() {
            return new UpdatePersona(this);
        }
    }
}