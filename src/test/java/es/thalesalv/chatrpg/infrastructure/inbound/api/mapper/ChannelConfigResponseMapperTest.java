package es.thalesalv.chatrpg.infrastructure.inbound.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.util.List;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import es.thalesalv.chatrpg.core.application.command.channelconfig.CreateChannelConfigResult;
import es.thalesalv.chatrpg.core.application.command.channelconfig.UpdateChannelConfigResult;
import es.thalesalv.chatrpg.core.application.query.channelconfig.GetChannelConfigResult;
import es.thalesalv.chatrpg.core.application.query.channelconfig.GetChannelConfigResultFixture;
import es.thalesalv.chatrpg.core.application.query.channelconfig.SearchChannelConfigsResult;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.ChannelConfigResponse;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.CreateChannelConfigResponse;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.SearchChannelConfigsResponse;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.UpdateChannelConfigResponse;

@ExtendWith(MockitoExtension.class)
public class ChannelConfigResponseMapperTest {

    @InjectMocks
    private ChannelConfigResponseMapper mapper;

    @Test
    public void searchChannelConfigResultToResponse() {

        // Given
        List<GetChannelConfigResult> results = Lists.list(GetChannelConfigResultFixture.sample().build(),
                GetChannelConfigResultFixture.sample().build());

        SearchChannelConfigsResult result = SearchChannelConfigsResult.builder()
                .page(1)
                .totalPages(2)
                .totalItems(20)
                .items(10)
                .results(results)
                .build();

        // When
        SearchChannelConfigsResponse response = mapper.toResponse(result);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getPage()).isEqualTo(result.getPage());
        assertThat(response.getTotalPages()).isEqualTo(result.getTotalPages());
        assertThat(response.getResultsInPage()).isEqualTo(result.getItems());
        assertThat(response.getTotalResults()).isEqualTo(result.getTotalItems());
    }

    @Test
    public void getChannelConfigResultToResponse() {

        // Given
        GetChannelConfigResult result = GetChannelConfigResultFixture.sample().build();

        // When
        ChannelConfigResponse response = mapper.toResponse(result);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(result.getId());
        assertThat(response.getName()).isEqualTo(result.getName());
        assertThat(response.getPersonaId()).isEqualTo(result.getPersonaId());
        assertThat(response.getWorldId()).isEqualTo(result.getWorldId());
        assertThat(response.getMaxTokenLimit()).isEqualTo(result.getMaxTokenLimit());
        assertThat(response.getTemperature()).isEqualTo(result.getTemperature());
        assertThat(response.getFrequencyPenalty()).isEqualTo(result.getFrequencyPenalty());
        assertThat(response.getMessageHistorySize()).isEqualTo(result.getMessageHistorySize());
        assertThat(response.getModeration()).isEqualTo(result.getModeration());
        assertThat(response.getPresencePenalty()).isEqualTo(result.getPresencePenalty());
        assertThat(response.getVisibility()).isEqualTo(result.getVisibility());
        assertThat(response.getOwnerDiscordId()).isEqualTo(result.getOwnerDiscordId());
        assertThat(response.getCreationDate()).isEqualTo(result.getCreationDate());
        assertThat(response.getLastUpdateDate()).isEqualTo(result.getLastUpdateDate());
        assertThat(response.getStopSequences()).hasSameElementsAs(result.getStopSequences());
        assertThat(response.getUsersAllowedToRead()).hasSameElementsAs(result.getUsersAllowedToRead());
        assertThat(response.getUsersAllowedToWrite()).hasSameElementsAs(result.getUsersAllowedToWrite());
        assertThat(response.getLogitBias()).containsAllEntriesOf(result.getLogitBias());
    }

    @Test
    public void createChannelConfigResultToResponse() {

        // Given
        CreateChannelConfigResult result = CreateChannelConfigResult.build("WRLDID");

        // When
        CreateChannelConfigResponse response = mapper.toResponse(result);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(result.getId());
    }

    @Test
    public void updateChannelConfigResultToResponse() {

        // Given
        UpdateChannelConfigResult result = UpdateChannelConfigResult.build(OffsetDateTime.now());

        // When
        UpdateChannelConfigResponse response = mapper.toResponse(result);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getLastUpdateDate()).isEqualTo(result.getLastUpdatedDateTime());
    }
}