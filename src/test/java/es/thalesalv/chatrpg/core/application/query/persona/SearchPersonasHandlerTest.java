package es.thalesalv.chatrpg.core.application.query.persona;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.thalesalv.chatrpg.core.domain.persona.PersonaRepository;

@ExtendWith(MockitoExtension.class)
public class SearchPersonasHandlerTest {

    @Mock
    private PersonaRepository repository;

    @InjectMocks
    private SearchPersonasHandler handler;

    @Test
    public void searchPersonas() {

        // Given
        SearchPersonas query = SearchPersonas.builder()
                .direction("ASC")
                .page(1)
                .items(2)
                .sortByField("name")
                .build();

        SearchPersonasResult expectedResult = SearchPersonasResult.builder()
                .page(1)
                .items(2)
                .build();

        when(repository.searchPersonas(any(SearchPersonas.class)))
                .thenReturn(expectedResult);

        // When
        SearchPersonasResult result = handler.handle(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getItems()).isEqualTo(expectedResult.getItems());
        assertThat(result.getPage()).isEqualTo(expectedResult.getPage());
    }
}