package me.moirai.discordbot.core.application.usecase.persona.request;

import me.moirai.discordbot.common.usecases.UseCase;
import me.moirai.discordbot.core.application.usecase.persona.result.SearchPersonasResult;

public final class SearchPersonas extends UseCase<SearchPersonasResult> {

    private final String name;
    private final String ownerDiscordId;
    private final boolean favorites;
    private final Integer page;
    private final Integer size;
    private final String sortingField;
    private final String direction;
    private final String visibility;
    private final String operation;
    private final String requesterDiscordId;

    private SearchPersonas(Builder builder) {

        this.name = builder.name;
        this.ownerDiscordId = builder.ownerDiscordId;
        this.favorites = builder.favorites;
        this.page = builder.page;
        this.size = builder.size;
        this.sortingField = builder.sortingField;
        this.direction = builder.direction;
        this.visibility = builder.visibility;
        this.operation = builder.operation;
        this.requesterDiscordId = builder.requesterDiscordId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public String getOwnerDiscordId() {
        return ownerDiscordId;
    }

    public boolean isFavorites() {
        return favorites;
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

    public String getVisibility() {
        return visibility;
    }

    public String getOperation() {
        return operation;
    }

    public String getRequesterDiscordId() {
        return requesterDiscordId;
    }

    public static final class Builder {

        private String name;
        private String ownerDiscordId;
        private boolean favorites;
        private Integer page;
        private Integer size;
        private String sortingField;
        private String direction;
        private String visibility;
        private String operation;
        private String requesterDiscordId;

        private Builder() {
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder ownerDiscordId(String ownerDiscordId) {
            this.ownerDiscordId = ownerDiscordId;
            return this;
        }

        public Builder favorites(boolean favorites) {
            this.favorites = favorites;
            return this;
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

        public Builder visibility(String visibility) {
            this.visibility = visibility;
            return this;
        }

        public Builder operation(String operation) {
            this.operation = operation;
            return this;
        }

        public Builder requesterDiscordId(String requesterDiscordId) {
            this.requesterDiscordId = requesterDiscordId;
            return this;
        }

        public SearchPersonas build() {
            return new SearchPersonas(this);
        }
    }
}