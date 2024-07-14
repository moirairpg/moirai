package es.thalesalv.chatrpg.core.application.usecase.world.request;

import java.util.List;

import es.thalesalv.chatrpg.common.usecases.UseCase;
import es.thalesalv.chatrpg.core.application.usecase.world.result.CreateWorldResult;
import reactor.core.publisher.Mono;

public final class CreateWorld extends UseCase<Mono<CreateWorldResult>> {

    private final String name;
    private final String description;
    private final String adventureStart;
    private final String visibility;
    private final List<CreateWorldLorebookEntry> lorebookEntries;
    private final List<String> usersAllowedToWrite;
    private final List<String> usersAllowedToRead;
    private final String requesterDiscordId;

    private CreateWorld(Builder builder) {
        this.name = builder.name;
        this.description = builder.description;
        this.adventureStart = builder.adventureStart;
        this.visibility = builder.visibility;
        this.lorebookEntries = builder.lorebookEntries;
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

    public String getDescription() {
        return description;
    }

    public String getAdventureStart() {
        return adventureStart;
    }

    public String getVisibility() {
        return visibility;
    }

    public List<CreateWorldLorebookEntry> getLorebookEntries() {
        return lorebookEntries;
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
        private String description;
        private String adventureStart;
        private String visibility;
        private List<CreateWorldLorebookEntry> lorebookEntries;
        private List<String> usersAllowedToWrite;
        private List<String> usersAllowedToRead;
        private String requesterDiscordId;

        private Builder() {
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

        public Builder lorebookEntries(List<CreateWorldLorebookEntry> lorebookEntries) {
            this.lorebookEntries = lorebookEntries;
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

        public CreateWorld build() {
            return new CreateWorld(this);
        }
    }
}