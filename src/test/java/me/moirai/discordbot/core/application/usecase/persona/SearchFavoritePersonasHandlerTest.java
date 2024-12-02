package me.moirai.discordbot.core.application.usecase.persona;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.discordbot.core.application.port.PersonaQueryRepository;
import me.moirai.discordbot.core.application.usecase.persona.request.SearchFavoritePersonas;
import me.moirai.discordbot.core.application.usecase.persona.result.SearchPersonasResult;

@ExtendWith(MockitoExtension.class)
public class SearchFavoritePersonasHandlerTest {

    @Mock
    private PersonaQueryRepository repository;

    @InjectMocks
    private SearchFavoritePersonasHandler handler;

    @Test
    public void searchPersonas() {

        // Given
        SearchFavoritePersonas query = SearchFavoritePersonas.builder()
                .direction("ASC")
                .page(1)
                .items(2)
                .sortByField("name")
                .build();

        SearchPersonasResult expectedResult = SearchPersonasResult.builder()
                .page(1)
                .items(2)
                .build();

        when(repository.search(any(SearchFavoritePersonas.class)))
                .thenReturn(expectedResult);

        // When
        SearchPersonasResult result = handler.handle(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getItems()).isEqualTo(expectedResult.getItems());
        assertThat(result.getPage()).isEqualTo(expectedResult.getPage());
    }
}