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
import me.moirai.discordbot.core.application.usecase.persona.request.SearchPersonas;
import me.moirai.discordbot.core.application.usecase.persona.result.SearchPersonasResult;

@ExtendWith(MockitoExtension.class)
public class SearchPersonasHandlerTest {

    @Mock
    private PersonaQueryRepository repository;

    @InjectMocks
    private SearchPersonasHandler handler;

    @Test
    public void searchPersonas() {

        // Given
        SearchPersonas query = SearchPersonas.builder()
                .direction("ASC")
                .page(1)
                .size(2)
                .sortingField("name")
                .build();

        SearchPersonasResult expectedResult = SearchPersonasResult.builder()
                .page(1)
                .items(2)
                .build();

        when(repository.search(any(SearchPersonas.class)))
                .thenReturn(expectedResult);

        // When
        SearchPersonasResult result = handler.handle(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getItems()).isEqualTo(expectedResult.getItems());
        assertThat(result.getPage()).isEqualTo(expectedResult.getPage());
    }
}