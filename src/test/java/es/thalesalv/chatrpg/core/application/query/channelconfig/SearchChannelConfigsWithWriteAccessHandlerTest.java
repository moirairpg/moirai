package es.thalesalv.chatrpg.core.application.query.channelconfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfigRepository;

@ExtendWith(MockitoExtension.class)
public class SearchChannelConfigsWithWriteAccessHandlerTest {

    @Mock
    private ChannelConfigRepository repository;

    @InjectMocks
    private SearchChannelConfigsWithWriteAccessHandler handler;

    @Test
    public void searchChannelConfigs() {

        // Given
        SearchChannelConfigsWithWriteAccess query = SearchChannelConfigsWithWriteAccess.builder()
                .direction("ASC")
                .page(1)
                .items(2)
                .sortByField("name")
                .build();

        SearchChannelConfigsResult expectedResult = SearchChannelConfigsResult.builder()
                .page(1)
                .items(2)
                .build();

        when(repository.searchChannelConfigsWithWriteAccess(any(SearchChannelConfigsWithWriteAccess.class)))
                .thenReturn(expectedResult);

        // When
        SearchChannelConfigsResult result = handler.handle(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getItems()).isEqualTo(expectedResult.getItems());
        assertThat(result.getPage()).isEqualTo(expectedResult.getPage());
    }
}