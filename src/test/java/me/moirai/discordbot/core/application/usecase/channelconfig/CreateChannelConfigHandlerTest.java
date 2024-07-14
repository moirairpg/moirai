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

import me.moirai.discordbot.core.application.usecase.channelconfig.request.CreateChannelConfig;
import me.moirai.discordbot.core.application.usecase.channelconfig.request.CreateChannelConfigFixture;
import me.moirai.discordbot.core.application.usecase.channelconfig.result.CreateChannelConfigResult;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfig;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfigFixture;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfigService;

@ExtendWith(MockitoExtension.class)
public class CreateChannelConfigHandlerTest {

    @Mock
    private ChannelConfigService domainService;

    @InjectMocks
    private CreateChannelConfigHandler handler;

    @Test
    public void errorWhenCommandIsNull() {

        // Given
        CreateChannelConfig command = null;

        // Then
        assertThrows(IllegalArgumentException.class, () -> handler.handle(command));
    }

    @Test
    public void createChannelConfig() {

        // Given
        String id = "HAUDHUAHD";
        ChannelConfig channelConfig = ChannelConfigFixture.sample().id(id).build();
        CreateChannelConfig command = CreateChannelConfigFixture.sample().build();

        when(domainService.createFrom(any(CreateChannelConfig.class)))
                .thenReturn(channelConfig);

        // When
        CreateChannelConfigResult result = handler.handle(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
    }
}
