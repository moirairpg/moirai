package me.moirai.discordbot.core.application.usecase.channelconfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.discordbot.common.exception.AssetAccessDeniedException;
import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.core.application.port.ChannelConfigQueryRepository;
import me.moirai.discordbot.core.application.usecase.channelconfig.request.GetChannelConfigById;
import me.moirai.discordbot.core.application.usecase.channelconfig.result.GetChannelConfigResult;
import me.moirai.discordbot.core.domain.PermissionsFixture;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfig;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfigFixture;

@ExtendWith(MockitoExtension.class)
public class GetChannelConfigByIdHandlerTest {

    @Mock
    private ChannelConfigQueryRepository queryRepository;

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
        ChannelConfig channelConfig = ChannelConfigFixture.sample()
                .id(id)
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterId)
                        .build())
                .build();

        GetChannelConfigById query = GetChannelConfigById.build(id, requesterId);

        when(queryRepository.findById(anyString())).thenReturn(Optional.of(channelConfig));

        // When
        GetChannelConfigResult result = handler.handle(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
    }

    @Test
    public void findChannelConfig_whenInvalidPermission_thenThrowException() {

        // Given
        String id = "CHCONFID";
        String requesterId = "RQSTRID";
        GetChannelConfigById query = GetChannelConfigById.build(id, requesterId);

        ChannelConfig persona = ChannelConfigFixture.sample()
                .id(id)
                .name("New name")
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId("ANTHRUSR")
                        .usersAllowedToRead(Collections.emptyList())
                        .build())
                .build();

        when(queryRepository.findById(anyString())).thenReturn(Optional.of(persona));

        // Then
        assertThrows(AssetAccessDeniedException.class, () -> handler.handle(query));
    }

    @Test
    public void findChannelConfig_whenChannelConfigNotFound_thenThrowException() {

        // Given
        String id = "CHCONFID";
        String requesterId = "RQSTRID";
        GetChannelConfigById query = GetChannelConfigById.build(id, requesterId);

        when(queryRepository.findById(anyString())).thenReturn(Optional.empty());

        // Then
        assertThrows(AssetNotFoundException.class, () -> handler.handle(query));
    }

    @Test
    public void findChannelConfig_whenValidId_thenChannelConfigIsReturned() {

        // Given
        String id = "CHCONFID";
        String requesterId = "RQSTRID";
        GetChannelConfigById query = GetChannelConfigById.build(id, requesterId);

        ChannelConfig persona = ChannelConfigFixture.sample()
                .id(id)
                .name("New name")
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterId)
                        .build())
                .build();

        when(queryRepository.findById(anyString())).thenReturn(Optional.of(persona));

        // When
        GetChannelConfigResult result = handler.handle(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(persona.getName());
    }
}