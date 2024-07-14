package es.thalesalv.chatrpg.core.application.usecase.persona.request;

import es.thalesalv.chatrpg.common.usecases.UseCase;
import es.thalesalv.chatrpg.core.application.usecase.persona.result.SearchPersonasResult;

public final class SearchPersonasWithReadAccess extends UseCase<SearchPersonasResult> {

    private final Integer page;
    private final Integer items;
    private final String searchField;
    private final String searchCriteria;
    private final String sortByField;
    private final String direction;
    private final String name;
    private final String gameMode;
    private final String requesterDiscordId;

    private SearchPersonasWithReadAccess(Builder builder) {

        this.page = builder.page;
        this.items = builder.items;
        this.searchField = builder.searchField;
        this.searchCriteria = builder.searchCriteria;
        this.sortByField = builder.sortByField;
        this.direction = builder.direction;
        this.name = builder.name;
        this.gameMode = builder.gameMode;
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

    public String getSearchField() {
        return searchField;
    }

    public String getSearchCriteria() {
        return searchCriteria;
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

    public String getGameMode() {
        return gameMode;
    }

    public String getRequesterDiscordId() {
        return requesterDiscordId;
    }

    public static final class Builder {

        private Integer page;
        private Integer items;
        private String searchField;
        private String searchCriteria;
        private String sortByField;
        private String direction;
        private String name;
        private String gameMode;
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

        public Builder searchField(String searchField) {
            this.searchField = searchField;
            return this;
        }

        public Builder searchCriteria(String searchCriteria) {
            this.searchCriteria = searchCriteria;
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

        public Builder gameMode(String gameMode) {
            this.gameMode = gameMode;
            return this;
        }

        public Builder requesterDiscordId(String requesterDiscordId) {
            this.requesterDiscordId = requesterDiscordId;
            return this;
        }

        public SearchPersonasWithReadAccess build() {
            return new SearchPersonasWithReadAccess(this);
        }
    }
}