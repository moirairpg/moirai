package es.thalesalv.chatrpg;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.reactive.server.WebTestClient;

import es.thalesalv.chatrpg.common.usecases.UseCaseRunner;
import es.thalesalv.chatrpg.infrastructure.security.authentication.DiscordPrincipal;
import es.thalesalv.chatrpg.infrastructure.security.authentication.DiscordUserDetailsService;
import reactor.core.publisher.Mono;

@WebFluxTest
@ExtendWith(MockitoExtension.class)
public class AbstractRestWebTest {

    @MockBean
    protected UseCaseRunner useCaseRunner;

    @MockBean
    protected ServerHttpSecurity serverHttpSecurity;

    @MockBean
    protected DiscordUserDetailsService discordUserDetailsService;

    @Autowired
    protected WebTestClient webTestClient;

    @BeforeEach
    public void before() throws Exception {

        UserDetails userDetails = DiscordPrincipal.builder()
                .id("USRID")
                .email("user@email.com")
                .username("username")
                .build();

        when(discordUserDetailsService.findByUsername(anyString())).thenReturn(Mono.just(userDetails));
    }
}
