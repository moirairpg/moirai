package me.moirai.discordbot.infrastructure.inbound.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.discordbot.core.application.usecase.discord.userdetails.DiscordUserDetailsResult;
import me.moirai.discordbot.infrastructure.inbound.api.response.UserDataResponse;

@ExtendWith(MockitoExtension.class)
public class UserDataResponseMapperTest {

    @InjectMocks
    private UserDataResponseMapper mapper;

    @Test
    public void mapUserDataResponse_whenValidData_thenObjectIsMapped() {

        // Given
        DiscordUserDetailsResult input = DiscordUserDetailsResult.builder()
                .id("1234")
                .avatar("https://img;com/avatar.jpg")
                .globalNickname("nickname")
                .username("username")
                .build();

        // When
        UserDataResponse result = mapper.toResponse(input);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(input.getId());
        assertThat(result.getAvatar()).isEqualTo(input.getAvatar());
        assertThat(result.getGlobalNickname()).isEqualTo(input.getGlobalNickname());
        assertThat(result.getUsername()).isEqualTo(input.getUsername());
    }
}
