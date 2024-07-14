package me.moirai.discordbot.core.application.usecase.world.request;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;

import me.moirai.discordbot.common.usecases.UseCase;
import me.moirai.discordbot.core.application.usecase.world.result.UpdateWorldResult;
import reactor.core.publisher.Mono;

public final class UpdateWorld extends UseCase<Mono<UpdateWorldResult>> {

    private final String id;
    private final String name;
    private final String description;
    private final String adventureStart;
    private final String visibility;
    private final List<String> usersAllowedToWriteToAdd;
    private final List<String> usersAllowedToWriteToRemove;
    private final List<String> usersAllowedToReadToAdd;
    private final List<String> usersAllowedToReadToRemove;
    private final String requesterDiscordId;

    private UpdateWorld(Builder builder) {

        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.adventureStart = builder.adventureStart;
        this.visibility = builder.visibility;
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

    public String getDescription() {
        return description;
    }

    public String getAdventureStart() {
        return adventureStart;
    }

    public String getVisibility() {
        return visibility;
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
        private String description;
        private String adventureStart;
        private String visibility;
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

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder adventureStart(String adventureStart) {
            this.adventureStart = adventureStart;
            return this;
        }

        public Builder visibility(String visibility) {
            this.visibility = visibility;
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

        public UpdateWorld build() {
            return new UpdateWorld(this);
        }
    }
}