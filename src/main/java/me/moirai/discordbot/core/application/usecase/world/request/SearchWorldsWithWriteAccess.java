package me.moirai.discordbot.core.application.usecase.world.request;

import me.moirai.discordbot.common.usecases.UseCase;
import me.moirai.discordbot.core.application.usecase.world.result.SearchWorldsResult;

public final class SearchWorldsWithWriteAccess extends UseCase<SearchWorldsResult> {

    private final Integer page;
    private final Integer items;
    private final String sortByField;
    private final String direction;
    private final String name;
    private final String requesterDiscordId;

    private SearchWorldsWithWriteAccess(Builder builder) {

        this.page = builder.page;
        this.items = builder.items;
        this.sortByField = builder.sortByField;
        this.direction = builder.direction;
        this.name = builder.name;
        this.requesterDiscordId = builder.requesterDiscordId;
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

    public String getName() {
        return name;
    }

    public String getRequesterDiscordId() {
        return requesterDiscordId;
    }

    public static final class Builder {

        private Integer page;
        private Integer items;
        private String sortByField;
        private String direction;
        private String name;
        private String requesterDiscordId;

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

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder requesterDiscordId(String requesterDiscordId) {
            this.requesterDiscordId = requesterDiscordId;
            return this;
        }

        public SearchWorldsWithWriteAccess build() {
            return new SearchWorldsWithWriteAccess(this);
        }
    }
}