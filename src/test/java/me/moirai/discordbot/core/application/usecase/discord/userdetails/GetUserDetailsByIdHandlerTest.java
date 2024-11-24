package me.moirai.discordbot.core.application.usecase.discord.userdetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import me.moirai.discordbot.AbstractDiscordTest;
import me.moirai.discordbot.core.application.port.DiscordUserDetailsPort;
import me.moirai.discordbot.core.application.usecase.discord.DiscordUserDetails;
import me.moirai.discordbot.core.application.usecase.discord.DiscordUserDetailsFixture;

public class GetUserDetailsByIdHandlerTest extends AbstractDiscordTest {

    @Mock
    private DiscordUserDetailsPort discordUserDetailsPort;

    @InjectMocks
    private GetUserDetailsByIdHandler handler;

    @Test
    public void retrieveUser_whenUserIsFound_thenReturnUserData() {

        // Given
        GetUserDetailsById query = GetUserDetailsById.build("1234");
        DiscordUserDetails user = DiscordUserDetailsFixture.create()
                .id(query.getDiscordUserId())
                .build();

        when(discordUserDetailsPort.getUserById(anyString())).thenReturn(Optional.of(user));

        // When
        DiscordUserDetailsResult result = handler.execute(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(query.getDiscordUserId());
        assertThat(result.getGlobalNickname()).isEqualTo("natalis");
        assertThat(result.getUsername()).isEqualTo("john.natalis");
    }
}
