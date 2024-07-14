package es.thalesalv.chatrpg.core.application.usecase.world.result;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;

public final class SearchWorldsResult {

    private final int page;
    private final int items;
    private final long totalItems;
    private final int totalPages;
    private final List<GetWorldResult> results;

    private SearchWorldsResult(Builder builder) {

        this.page = builder.page;
        this.items = builder.items;
        this.totalItems = builder.totalItems;
        this.totalPages = builder.totalPages;
        this.results = unmodifiableList(builder.results);
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getPage() {
        return page;
    }

    public int getItems() {
        return items;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public List<GetWorldResult> getResults() {
        return results;
    }

    public static final class Builder {

        private int page;
        private int items;
        private long totalItems;
        private int totalPages;
        private List<GetWorldResult> results = new ArrayList<>();

        private Builder() {
        }

        public Builder page(int page) {
            this.page = page;
            return this;
        }

        public Builder items(int items) {
            this.items = items;
            return this;
        }

        public Builder totalItems(long totalItems) {
            this.totalItems = totalItems;
            return this;
        }

        public Builder totalPages(int totalPages) {
            this.totalPages = totalPages;
            return this;
        }

        public Builder results(List<GetWorldResult> results) {

            if (results != null) {
                this.results = results;
            }

            return this;
        }

        public SearchWorldsResult build() {
            return new SearchWorldsResult(this);
        }
    }
}