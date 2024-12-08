package me.moirai.discordbot.core.application.usecase.adventure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.discordbot.core.application.port.AdventureQueryRepository;
import me.moirai.discordbot.core.application.usecase.adventure.request.SearchAdventuresWithReadAccess;
import me.moirai.discordbot.core.application.usecase.adventure.result.SearchAdventuresResult;

@ExtendWith(MockitoExtension.class)
public class SearchAdventuresWithReadAccessHandlerTest {

    @Mock
    private AdventureQueryRepository repository;

    @InjectMocks
    private SearchAdventuresWithReadAccessHandler handler;

    @Test
    public void searchAdventures() {

        // Given
        SearchAdventuresWithReadAccess query = SearchAdventuresWithReadAccess.builder()
                .direction("ASC")
                .page(1)
                .items(2)
                .sortByField("name")
                .build();

        SearchAdventuresResult expectedResult = SearchAdventuresResult.builder()
                .page(1)
                .items(2)
                .build();

        when(repository.search(any(SearchAdventuresWithReadAccess.class)))
                .thenReturn(expectedResult);

        // When
        SearchAdventuresResult result = handler.handle(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getItems()).isEqualTo(expectedResult.getItems());
        assertThat(result.getPage()).isEqualTo(expectedResult.getPage());
    }
}