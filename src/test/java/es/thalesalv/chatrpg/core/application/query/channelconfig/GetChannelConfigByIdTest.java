package es.thalesalv.chatrpg.core.application.query.channelconfig;

import static org.assertj.core.api.Assertions.assertThat;
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
public class GetChannelConfigByIdTest {

    @Mock
    private ChannelConfigRepository repository;

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
        ChannelConfig channelConfig = ChannelConfigFixture.sample().id(id).build();
        GetChannelConfigById query = GetChannelConfigById.build(id);

        when(repository.findById(anyString())).thenReturn(Optional.of(channelConfig));

        // When
        GetChannelConfigResult result = handler.handle(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
    }

    @Test
    public void errorWhenChannelConfigNotFound() {

        // Given
        String id = "HAUDHUAHD";
        GetChannelConfigById query = GetChannelConfigById.build(id);

        when(repository.findById(anyString())).thenReturn(Optional.empty());

        // Then
        assertThrows(AssetNotFoundException.class, () -> handler.handle(query));
    }
}