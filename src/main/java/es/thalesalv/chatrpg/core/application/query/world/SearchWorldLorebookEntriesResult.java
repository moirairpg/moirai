package es.thalesalv.chatrpg.core.application.query.world;

import java.util.List;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder(builderClassName = "Builder")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SearchWorldLorebookEntriesResult {

    private final int page;
    private final int items;
    private final long totalItems;
    private final int totalPages;
    private final List<GetWorldLorebookEntryResult> results;
}
