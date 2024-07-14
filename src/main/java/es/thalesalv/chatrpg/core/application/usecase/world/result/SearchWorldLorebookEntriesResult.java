package es.thalesalv.chatrpg.core.application.usecase.world.result;

import java.util.List;

public final class SearchWorldLorebookEntriesResult {

    private final int page;
    private final int items;
    private final long totalItems;
    private final int totalPages;
    private final List<GetWorldLorebookEntryResult> results;

    private SearchWorldLorebookEntriesResult(Builder builder) {
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

    public List<GetWorldLorebookEntryResult> getResults() {
        return results;
    }

    public static final class Builder {
        private int page;
        private int items;
        private long totalItems;
        private int totalPages;
        private List<GetWorldLorebookEntryResult> results;

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

        public Builder results(List<GetWorldLorebookEntryResult> val) {
            results = val;
            return this;
        }

        public SearchWorldLorebookEntriesResult build() {
            return new SearchWorldLorebookEntriesResult(this);
        }
    }
}