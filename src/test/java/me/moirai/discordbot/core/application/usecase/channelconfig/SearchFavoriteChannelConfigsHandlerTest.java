package me.moirai.discordbot.core.application.usecase.channelconfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.discordbot.core.application.port.ChannelConfigQueryRepository;
import me.moirai.discordbot.core.application.usecase.channelconfig.request.SearchFavoriteChannelConfigs;
import me.moirai.discordbot.core.application.usecase.channelconfig.result.SearchChannelConfigsResult;

@ExtendWith(MockitoExtension.class)
public class SearchFavoriteChannelConfigsHandlerTest {

    @Mock
    private ChannelConfigQueryRepository repository;

    @InjectMocks
    private SearchFavoriteChannelConfigsHandler handler;

    @Test
    public void searchChannelConfigs() {

        // Given
        SearchFavoriteChannelConfigs query = SearchFavoriteChannelConfigs.builder()
                .direction("ASC")
                .page(1)
                .items(2)
                .sortByField("name")
                .build();

        SearchChannelConfigsResult expectedResult = SearchChannelConfigsResult.builder()
                .page(1)
                .items(2)
                .build();

        when(repository.search(any(SearchFavoriteChannelConfigs.class)))
                .thenReturn(expectedResult);

        // When
        SearchChannelConfigsResult result = handler.handle(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getItems()).isEqualTo(expectedResult.getItems());
        assertThat(result.getPage()).isEqualTo(expectedResult.getPage());
    }
}