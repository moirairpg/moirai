package me.moirai.discordbot.core.application.usecase.adventure.request;

import me.moirai.discordbot.common.usecases.UseCase;
import me.moirai.discordbot.core.application.usecase.adventure.result.SearchAdventureLorebookEntriesResult;

public final class SearchAdventureLorebookEntries extends UseCase<SearchAdventureLorebookEntriesResult> {

    private final String name;
    private final String adventureId;
    private final Integer page;
    private final Integer size;
    private final String sortingField;
    private final String direction;
    private final String requesterDiscordId;

    private SearchAdventureLorebookEntries(Builder builder) {

        this.page = builder.page;
        this.size = builder.size;
        this.sortingField = builder.sortingField;
        this.direction = builder.direction;
        this.name = builder.name;
        this.adventureId = builder.adventureId;
        this.requesterDiscordId = builder.requesterDiscordId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Integer getPage() {
        return page;
    }

    public Integer getSize() {
        return size;
    }

    public String getSortingField() {
        return sortingField;
    }

    public String getDirection() {
        return direction;
    }

    public String getName() {
        return name;
    }

    public String getAdventureId() {
        return adventureId;
    }

    public String getRequesterDiscordId() {
        return requesterDiscordId;
    }

    public static final class Builder {

        private Integer page;
        private Integer size;
        private String sortingField;
        private String direction;
        private String name;
        private String adventureId;
        private String requesterDiscordId;

        private Builder() {
        }

        public Builder page(Integer page) {
            this.page = page;
            return this;
        }

        public Builder size(Integer size) {
            this.size = size;
            return this;
        }

        public Builder sortingField(String sortingField) {
            this.sortingField = sortingField;
            return this;
        }

        public Builder direction(String direction) {
            this.direction = direction;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder adventureId(String adventureId) {
            this.adventureId = adventureId;
            return this;
        }

        public Builder requesterDiscordId(String requesterDiscordId) {
            this.requesterDiscordId = requesterDiscordId;
            return this;
        }

        public SearchAdventureLorebookEntries build() {
            return new SearchAdventureLorebookEntries(this);
        }
    }
}