package me.moirai.discordbot.core.application.usecase.world;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.discordbot.core.application.usecase.world.request.SearchWorldLorebookEntries;
import me.moirai.discordbot.core.application.usecase.world.result.SearchWorldLorebookEntriesResult;
import me.moirai.discordbot.core.domain.world.WorldLorebookEntryRepository;

@ExtendWith(MockitoExtension.class)
public class SearchWorldLorebookEntriesHandlerTest {

    @Mock
    private WorldLorebookEntryRepository repository;

    @InjectMocks
    private SearchWorldLorebookEntriesHandler handler;

    @Test
    public void searchWorldLorebookEntrys() {

        // Given
        SearchWorldLorebookEntries query = SearchWorldLorebookEntries.builder()
                .direction("ASC")
                .page(1)
                .items(2)
                .sortByField("name")
                .build();

        SearchWorldLorebookEntriesResult expectedResult = SearchWorldLorebookEntriesResult.builder()
                .page(1)
                .items(2)
                .build();

        when(repository.searchBy(any(SearchWorldLorebookEntries.class)))
                .thenReturn(expectedResult);

        // When
        SearchWorldLorebookEntriesResult result = handler.handle(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getItems()).isEqualTo(expectedResult.getItems());
        assertThat(result.getPage()).isEqualTo(expectedResult.getPage());
    }
}
