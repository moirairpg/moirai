package es.thalesalv.chatrpg.core.application.query.world;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public final class GetWorldByIdResult {

    private final String id;
    private final String name;
    private final String description;
    private final String adventureStart;
    private final List<GetWorldLorebookEntry> lorebook;
    private final String visibility;
    private final String ownerDiscordId;
    private final List<String> writerUsers;
    private final List<String> readerUsers;
    private final String creatorDiscordId;
    private final OffsetDateTime creationDate;
    private final OffsetDateTime lastUpdateDate;

    private GetWorldByIdResult(Builder builder) {

        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.adventureStart = builder.adventureStart;
        this.visibility = builder.visibility;
        this.ownerDiscordId = builder.ownerDiscordId;
        this.writerUsers = builder.writerUsers;
        this.readerUsers = builder.readerUsers;
        this.creatorDiscordId = builder.creatorDiscordId;
        this.creationDate = builder.creationDate;
        this.lastUpdateDate = builder.lastUpdateDate;

        List<GetWorldLorebookEntry> lorebook = builder.lorebook == null ? Collections.emptyList() : builder.lorebook;
        this.lorebook = Collections.unmodifiableList(lorebook);
    }

    public static Builder builder() {

        return new Builder();
    }

    public List<GetWorldLorebookEntry> getLorebook() {

        return lorebook;
    }

    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Builder {

        private String id;
        private String name;
        private String description;
        private String adventureStart;
        private List<GetWorldLorebookEntry> lorebook;
        private String visibility;
        private String ownerDiscordId;
        private List<String> writerUsers;
        private List<String> readerUsers;
        private String creatorDiscordId;
        private OffsetDateTime creationDate;
        private OffsetDateTime lastUpdateDate;

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

        public Builder lorebook(List<GetWorldLorebookEntry> lorebook) {

            this.lorebook = lorebook;
            return this;
        }

        public Builder visibility(String visibility) {

            this.visibility = visibility;
            return this;
        }

        public Builder ownerDiscordId(String ownerDiscordId) {

            this.ownerDiscordId = ownerDiscordId;
            return this;
        }

        public Builder writerUsers(List<String> writerUsers) {

            this.writerUsers = writerUsers;
            return this;
        }

        public Builder readerUsers(List<String> readerUsers) {

            this.readerUsers = readerUsers;
            return this;
        }

        public Builder creatorDiscordId(String creatorDiscordId) {

            this.creatorDiscordId = creatorDiscordId;
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

        public GetWorldByIdResult build() {

            return new GetWorldByIdResult(this);
        }
    }
}
