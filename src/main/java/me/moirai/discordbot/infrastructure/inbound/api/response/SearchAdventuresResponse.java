package me.moirai.discordbot.infrastructure.inbound.api.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchAdventuresResponse {

    private int page;
    private int totalPages;
    private int resultsInPage;
    private long totalResults;
    private List<AdventureResponse> results;

    public SearchAdventuresResponse() {
    }

    private SearchAdventuresResponse(Builder builder) {
        this.page = builder.page;
        this.totalPages = builder.totalPages;
        this.resultsInPage = builder.resultsInPage;
        this.totalResults = builder.totalResults;
        this.results = builder.results;
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getPage() {
        return page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getResultsInPage() {
        return resultsInPage;
    }

    public long getTotalResults() {
        return totalResults;
    }

    public List<AdventureResponse> getResults() {
        return results;
    }

    public static final class Builder {
        private int page;
        private int totalPages;
        private int resultsInPage;
        private long totalResults;
        private List<AdventureResponse> results;

        private Builder() {
        }

        public Builder page(int page) {
            this.page = page;
            return this;
        }

        public Builder totalPages(int totalPages) {
            this.totalPages = totalPages;
            return this;
        }

        public Builder resultsInPage(int resultsInPage) {
            this.resultsInPage = resultsInPage;
            return this;
        }

        public Builder totalResults(long totalResults) {
            this.totalResults = totalResults;
            return this;
        }

        public Builder results(List<AdventureResponse> results) {
            this.results = results;
            return this;
        }

        public SearchAdventuresResponse build() {
            return new SearchAdventuresResponse(this);
        }
    }
}
