package me.moirai.discordbot.core.application.usecase.persona.request;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;

import me.moirai.discordbot.common.usecases.UseCase;
import me.moirai.discordbot.core.application.usecase.persona.result.CreatePersonaResult;
import reactor.core.publisher.Mono;

public final class CreatePersona extends UseCase<Mono<CreatePersonaResult>> {

    private final String name;
    private final String personality;
    private final String visibility;
    private final String requesterDiscordId;
    private final List<String> usersAllowedToWrite;
    private final List<String> usersAllowedToRead;

    public CreatePersona(Builder builder) {
        this.name = builder.name;
        this.personality = builder.personality;
        this.visibility = builder.visibility;
        this.requesterDiscordId = builder.requesterDiscordId;
        this.usersAllowedToWrite = unmodifiableList(builder.usersAllowedToWrite);
        this.usersAllowedToRead = unmodifiableList(builder.usersAllowedToRead);
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

    public String getVisibility() {
        return visibility;
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
        private String visibility;
        private String requesterDiscordId;
        private List<String> usersAllowedToWrite = new ArrayList<>();
        private List<String> usersAllowedToRead = new ArrayList<>();

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

        public Builder visibility(String visibility) {
            this.visibility = visibility;
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

        public Builder requesterDiscordId(String requesterDiscordId) {
            this.requesterDiscordId = requesterDiscordId;
            return this;
        }

        public CreatePersona build() {
            return new CreatePersona(this);
        }
    }
}
