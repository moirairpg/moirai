package es.thalesalv.chatrpg.core.application.usecase.world.result;

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
        this.results = builder.results;
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
        private List<GetWorldResult> results;

        private Builder() {
        }

        public Builder page(int val) {
            page = val;
            return this;
        }

        public Builder items(int val) {
            items = val;
            return this;
        }

        public Builder totalItems(long val) {
            totalItems = val;
            return this;
        }

        public Builder totalPages(int val) {
            totalPages = val;
            return this;
        }

        public Builder results(List<GetWorldResult> val) {
            results = val;
            return this;
        }

        public SearchWorldsResult build() {
            return new SearchWorldsResult(this);
        }
    }
}