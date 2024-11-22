package me.moirai.discordbot.core.application.usecase.channelconfig;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.discordbot.core.application.usecase.channelconfig.request.DeleteChannelConfig;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfigService;

@ExtendWith(MockitoExtension.class)
public class DeleteChannelConfigHandlerTest {

    @Mock
    private ChannelConfigService domainService;

    @InjectMocks
    private DeleteChannelConfigHandler handler;

    @Test
    public void errorWhenIdIsNull() {

        // Given
        String id = null;
        String requesterId = "RQSTRID";
        DeleteChannelConfig config = DeleteChannelConfig.build(id, requesterId);

        // Then
        assertThrows(IllegalArgumentException.class, () -> handler.handle(config));
    }

    @Test
    public void deleteChannelConfig() {

        // Given
        String id = "CHCONFID";
        String requesterId = "RQSTRID";

        DeleteChannelConfig command = DeleteChannelConfig.build(id, requesterId);

        doNothing().when(domainService).delete(command);

        // When
        handler.handle(command);

        // Then
        verify(domainService, times(1)).delete(any());
    }
}
