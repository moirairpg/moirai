package es.thalesalv.chatrpg.core.application.usecase.channelconfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.thalesalv.chatrpg.core.application.usecase.channelconfig.request.UpdateChannelConfig;
import es.thalesalv.chatrpg.core.application.usecase.channelconfig.result.UpdateChannelConfigResult;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfig;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfigFixture;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfigServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UpdateChannelConfigHandlerTest {

    @Mock
    private ChannelConfigServiceImpl service;

    @InjectMocks
    private UpdateChannelConfigHandler handler;

    @Test
    public void updateChannelConfig() {

        // Given
        String id = "CHCONFID";

        UpdateChannelConfig command = UpdateChannelConfig.builder()
                .id(id)
                .name("Name")
                .worldId("WRLDID")
                .personaId("PRSNID")
                .moderation("STRICT")
                .visibility("PRIVATE")
                .build();

        ChannelConfig expectedUpdatedChannelConfig = ChannelConfigFixture.sample().build();

        when(service.update(any(UpdateChannelConfig.class)))
                .thenReturn(expectedUpdatedChannelConfig);

        // When
        UpdateChannelConfigResult result = handler.handle(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getLastUpdatedDateTime()).isEqualTo(expectedUpdatedChannelConfig.getLastUpdateDate());
    }
}
