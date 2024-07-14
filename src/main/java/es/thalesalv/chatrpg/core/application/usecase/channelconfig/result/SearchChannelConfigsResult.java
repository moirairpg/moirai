package es.thalesalv.chatrpg.core.application.usecase.channelconfig.result;

import java.util.List;

public final class SearchChannelConfigsResult {

    private final int page;
    private final int items;
    private final long totalItems;
    private final int totalPages;
    private final List<GetChannelConfigResult> results;

    private SearchChannelConfigsResult(Builder builder) {
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

    public List<GetChannelConfigResult> getResults() {
        return results;
    }

    public static final class Builder {

        private int page;
        private int items;
        private long totalItems;
        private int totalPages;
        private List<GetChannelConfigResult> results;

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

        public Builder results(List<GetChannelConfigResult> results) {
            this.results = results;
            return this;
        }

        public SearchChannelConfigsResult build() {
            return new SearchChannelConfigsResult(this);
        }
    }
}
