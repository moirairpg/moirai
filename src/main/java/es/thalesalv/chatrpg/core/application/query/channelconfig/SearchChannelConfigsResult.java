package es.thalesalv.chatrpg.core.application.query.channelconfig;

import java.util.List;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder(builderClassName = "Builder")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SearchChannelConfigsResult {

    private final Integer page;
    private final Integer results;
    private final List<GetChannelConfigResult> channelConfigs;

    public SearchChannelConfigsResult(Builder builder) {
        this.page = builder.page;
        this.results = builder.results;
        this.channelConfigs = builder.channelConfigs;
    }

    public static class Builder {

        private Integer page;
        private Integer results;
        private List<GetChannelConfigResult> channelConfigs;

        public SearchChannelConfigsResult build() {

            return new SearchChannelConfigsResult(this);
        }
    }
}
