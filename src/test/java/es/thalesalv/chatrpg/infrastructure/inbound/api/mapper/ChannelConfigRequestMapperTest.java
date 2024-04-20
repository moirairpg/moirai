package es.thalesalv.chatrpg.infrastructure.inbound.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import es.thalesalv.chatrpg.core.application.command.channelconfig.CreateChannelConfig;
import es.thalesalv.chatrpg.core.application.command.channelconfig.DeleteChannelConfig;
import es.thalesalv.chatrpg.core.application.command.channelconfig.UpdateChannelConfig;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.CreateChannelConfigRequest;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.CreateChannelConfigRequestFixture;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.UpdateChannelConfigRequest;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.UpdateChannelConfigRequestFixture;

@ExtendWith(MockitoExtension.class)
public class ChannelConfigRequestMapperTest {

    @InjectMocks
    private ChannelConfigRequestMapper mapper;

    @Test
    public void creationRequestToCommand() {

        // Given
        String requesterId = "RQSTRID";
        CreateChannelConfigRequest request = CreateChannelConfigRequestFixture.sample().build();

        // When
        CreateChannelConfig command = mapper.toCommand(request, requesterId);

        // Then
        assertThat(command).isNotNull();
        assertThat(command.getName()).isEqualTo(request.getName());
        assertThat(command.getPersonaId()).isEqualTo(request.getPersonaId());
        assertThat(command.getWorldId()).isEqualTo(request.getWorldId());
        assertThat(command.getMaxTokenLimit()).isEqualTo(request.getMaxTokenLimit());
        assertThat(command.getTemperature()).isEqualTo(request.getTemperature());
        assertThat(command.getFrequencyPenalty()).isEqualTo(request.getFrequencyPenalty());
        assertThat(command.getModeration()).isEqualTo(request.getModeration());
        assertThat(command.getPresencePenalty()).isEqualTo(request.getPresencePenalty());
        assertThat(command.getVisibility()).isEqualTo(request.getVisibility());
        assertThat(command.getStopSequences()).hasSameElementsAs(request.getStopSequences());
        assertThat(command.getUsersAllowedToRead()).hasSameElementsAs(request.getUsersAllowedToRead());
        assertThat(command.getUsersAllowedToWrite()).hasSameElementsAs(request.getUsersAllowedToWrite());
        assertThat(command.getLogitBias()).containsAllEntriesOf(request.getLogitBias());
    }

    @Test
    public void updateRequestToCommand() {

        // Given
        String channelConfigId = "WRLDID";
        String requesterId = "RQSTRID";
        UpdateChannelConfigRequest request = UpdateChannelConfigRequestFixture.sample().build();

        // When
        UpdateChannelConfig command = mapper.toCommand(request, channelConfigId, requesterId);

        // Then
        assertThat(command).isNotNull();
        assertThat(command.getId()).isEqualTo(channelConfigId);
        assertThat(command.getName()).isEqualTo(request.getName());
        assertThat(command.getPersonaId()).isEqualTo(request.getPersonaId());
        assertThat(command.getWorldId()).isEqualTo(request.getWorldId());
        assertThat(command.getMaxTokenLimit()).isEqualTo(request.getMaxTokenLimit());
        assertThat(command.getTemperature()).isEqualTo(request.getTemperature());
        assertThat(command.getFrequencyPenalty()).isEqualTo(request.getFrequencyPenalty());
        assertThat(command.getModeration()).isEqualTo(request.getModeration());
        assertThat(command.getPresencePenalty()).isEqualTo(request.getPresencePenalty());
        assertThat(command.getVisibility()).isEqualTo(request.getVisibility());
        assertThat(command.getStopSequencesToAdd()).hasSameElementsAs(request.getStopSequencesToAdd());
        assertThat(command.getStopSequencesToRemove()).hasSameElementsAs(request.getStopSequencesToRemove());
        assertThat(command.getUsersAllowedToReadToAdd()).hasSameElementsAs(request.getUsersAllowedToReadToAdd());
        assertThat(command.getUsersAllowedToWriteToAdd()).hasSameElementsAs(request.getUsersAllowedToWriteToAdd());
        assertThat(command.getUsersAllowedToReadToRemove()).hasSameElementsAs(request.getUsersAllowedToReadToRemove());
        assertThat(command.getUsersAllowedToWriteToRemove()).hasSameElementsAs(request.getUsersAllowedToWriteToRemove());
        assertThat(command.getLogitBiasToAdd()).containsAllEntriesOf(request.getLogitBiasToAdd());
        assertThat(command.getLogitBiasToRemove()).hasSameElementsAs(request.getLogitBiasToRemove());
    }

    @Test
    public void deleteRequestToCommand() {

        // Given
        String channelConfigId = "WRLDID";
        String requesterId = "RQSTRID";

        // When
        DeleteChannelConfig command = mapper.toCommand(channelConfigId, requesterId);

        // Then
        assertThat(command).isNotNull();
        assertThat(command.getId()).isEqualTo(channelConfigId);
        assertThat(command.getRequesterDiscordId()).isEqualTo(requesterId);
    }
}
