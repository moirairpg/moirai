package es.thalesalv.chatrpg.core.application.command.channelconfig;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.thalesalv.chatrpg.common.exception.AssetNotFoundException;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfig;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfigFixture;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfigRepository;

@ExtendWith(MockitoExtension.class)
public class DeleteChannelConfigHandlerTest {

    @Mock
    private ChannelConfigRepository repository;

    @InjectMocks
    private DeleteChannelConfigHandler handler;

    @Test
    public void errorWhenAssetNotFound() {

        // Given
        String id = "CHCONFID";

        DeleteChannelConfig command = DeleteChannelConfig.build(id);

        when(repository.findById(anyString(), anyString())).thenReturn(Optional.empty());

        // Then
        assertThrows(AssetNotFoundException.class, () -> handler.handle(command));
    }

    @Test
    public void errorWhenIdIsNull() {

        // Given
        String id = null;

        DeleteChannelConfig config = DeleteChannelConfig.build(id);

        // Then
        assertThrows(IllegalArgumentException.class, () -> handler.handle(config));
    }

    @Test
    public void deleteChannelConfig() {

        // Given
        String id = "CHCONFID";

        ChannelConfig channelConfig = ChannelConfigFixture.sample().id(id).build();

        DeleteChannelConfig command = DeleteChannelConfig.build(id);

        when(repository.findById(anyString(), anyString())).thenReturn(Optional.of(channelConfig));

        // Then
        handler.handle(command);
    }
}
