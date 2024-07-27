package me.moirai.discordbot.core.application.usecase.channelconfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.discordbot.core.application.usecase.channelconfig.request.GetChannelConfigById;
import me.moirai.discordbot.core.application.usecase.channelconfig.result.GetChannelConfigResult;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfig;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfigFixture;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfigService;

@ExtendWith(MockitoExtension.class)
public class GetChannelConfigByIdHandlerTest {

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