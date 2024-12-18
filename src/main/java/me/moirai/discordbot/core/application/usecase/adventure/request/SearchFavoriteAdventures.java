package me.moirai.discordbot.core.application.usecase.adventure.request;

import me.moirai.discordbot.common.usecases.UseCase;
import me.moirai.discordbot.core.application.usecase.adventure.result.SearchAdventuresResult;

public final class SearchFavoriteAdventures extends UseCase<SearchAdventuresResult> {

    private final Integer page;
    private final Integer items;
    private final String sortByField;
    private final String direction;
    private final String aiModel;
    private final String moderation;
    private final String name;
    private final String requesterDiscordId;
    private final String gameMode;
    private final String visibility;
    private final String worldId;
    private final String personaId;

    private SearchFavoriteAdventures(Builder builder) {

        this.page = builder.page;
        this.items = builder.items;
        this.sortByField = builder.sortByField;
        this.direction = builder.direction;
        this.aiModel = builder.aiModel;
        this.moderation = builder.moderation;
        this.name = builder.name;
        this.requesterDiscordId = builder.requesterDiscordId;
        this.gameMode = builder.gameMode;
        this.visibility = builder.visibility;
        this.worldId = builder.worldId;
        this.personaId = builder.personaId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Integer getPage() {
        return page;
    }

    public Integer getItems() {
        return items;
    }

    public String getSortByField() {
        return sortByField;
    }

    public String getDirection() {
        return direction;
    }

    public String getAiModel() {
        return aiModel;
    }

    public String getModeration() {
        return moderation;
    }

    public String getName() {
        return name;
    }

    public String getRequesterDiscordId() {
        return requesterDiscordId;
    }

    public String getGameMode() {
        return gameMode;
    }

    public String getVisibility() {
        return visibility;
    }

    public String getWorldId() {
        return worldId;
    }

    public String getPersonaId() {
        return personaId;
    }

    public static final class Builder {

        private Integer page;
        private Integer items;
        private String sortByField;
        private String direction;
        private String aiModel;
        private String moderation;
        private String name;
        private String requesterDiscordId;
        private String gameMode;
        private String visibility;
        private String worldId;
        private String personaId;

        private Builder() {
        }

        public Builder page(Integer page) {
            this.page = page;
            return this;
        }

        public Builder items(Integer items) {
            this.items = items;
            return this;
        }

        public Builder sortByField(String sortByField) {
            this.sortByField = sortByField;
            return this;
        }

        public Builder direction(String direction) {
            this.direction = direction;
            return this;
        }

        public Builder aiModel(String aiModel) {
            this.aiModel = aiModel;
            return this;
        }

        public Builder moderation(String moderation) {
            this.moderation = moderation;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder gameMode(String gameMode) {
            this.gameMode = gameMode;
            return this;
        }

        public Builder requesterDiscordId(String requesterDiscordId) {
            this.requesterDiscordId = requesterDiscordId;
            return this;
        }

        public Builder visibility(String visibility) {
            this.visibility = visibility;
            return this;
        }

        public Builder worldId(String worldId) {
            this.worldId = worldId;
            return this;
        }

        public Builder personaId(String personaId) {
            this.personaId = personaId;
            return this;
        }

        public SearchFavoriteAdventures build() {
            return new SearchFavoriteAdventures(this);
        }
    }
}