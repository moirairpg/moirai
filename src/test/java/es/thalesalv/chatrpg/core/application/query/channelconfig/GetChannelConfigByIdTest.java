package es.thalesalv.chatrpg.core.application.query.channelconfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfig;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfigService;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfigFixture;

@ExtendWith(MockitoExtension.class)
public class GetChannelConfigByIdTest {

    @Mock
    private ChannelConfigService domainService;

    @InjectMocks
    private GetChannelConfigByIdHandler handler;

    @Test
    public void errorWhenQueryIsNull() {

        // Given
        GetChannelConfigById query = null;

        // Then
        assertThrows(IllegalArgumentException.class, () -> handler.handle(query));
    }

    @Test
    public void getChannelConfigById() {

        // Given
        String id = "HAUDHUAHD";
        String requesterId = "RQSTRID";
        ChannelConfig channelConfig = ChannelConfigFixture.sample().id(id).build();
        GetChannelConfigById query = GetChannelConfigById.build(id, requesterId);

        when(domainService.getChannelConfigById(any(GetChannelConfigById.class))).thenReturn(channelConfig);

        // When
        GetChannelConfigResult result = handler.handle(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
    }
}