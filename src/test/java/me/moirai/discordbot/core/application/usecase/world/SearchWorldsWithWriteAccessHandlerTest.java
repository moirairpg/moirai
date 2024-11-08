package me.moirai.discordbot.core.application.usecase.world;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.discordbot.core.application.port.WorldQueryRepository;
import me.moirai.discordbot.core.application.usecase.world.request.SearchWorldsWithWriteAccess;
import me.moirai.discordbot.core.application.usecase.world.result.SearchWorldsResult;

@ExtendWith(MockitoExtension.class)
public class SearchWorldsWithWriteAccessHandlerTest {

    @Mock
    private WorldQueryRepository repository;

    @InjectMocks
    private SearchWorldsWithWriteAccessHandler handler;

    @Test
    public void searchWorlds() {

        // Given
        SearchWorldsWithWriteAccess query = SearchWorldsWithWriteAccess.builder()
                .direction("ASC")
                .page(1)
                .items(2)
                .sortByField("name")
                .build();

        SearchWorldsResult expectedResult = SearchWorldsResult.builder()
                .page(1)
                .items(2)
                .build();

        when(repository.searchWorldsWithWriteAccess(any(SearchWorldsWithWriteAccess.class)))
                .thenReturn(expectedResult);

        // When
        SearchWorldsResult result = handler.handle(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getItems()).isEqualTo(expectedResult.getItems());
        assertThat(result.getPage()).isEqualTo(expectedResult.getPage());
    }
}
