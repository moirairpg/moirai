package es.thalesalv.chatrpg.infrastructure.security.authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.thalesalv.chatrpg.core.application.port.DiscordUserDetailsPort;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.DiscordUserDataResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class DiscordUserDetailsServiceTest {

    @Mock
    private DiscordUserDetailsPort discordUserDetailsPort;

    @InjectMocks
    private DiscordUserDetailsService service;

    @Test
    public void createUserPrincipal() {

        // Given
        String token = "TOKEN";

        DiscordUserDataResponse response = DiscordUserDataResponse.builder()
                .displayName("displayName")
                .username("username")
                .email("email@email.com")
                .build();

        when(discordUserDetailsPort.retrieveLoggedUser(anyString())).thenReturn(Mono.just(response));

        // Then
        StepVerifier.create(service.findByUsername(token))
                .assertNext(userDetails -> {
                    assertThat(userDetails).isNotNull();
                    assertThat(userDetails.getUsername()).isEqualTo(response.getUsername());
                })
                .verifyComplete();
    }
}
