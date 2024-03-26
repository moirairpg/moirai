package es.thalesalv.chatrpg;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.reactive.server.WebTestClient;

import es.thalesalv.chatrpg.common.usecases.UseCaseRunner;
import es.thalesalv.chatrpg.core.application.port.DiscordAuthenticationPort;
import es.thalesalv.chatrpg.infrastructure.inbound.api.controller.AuthenticationController;
import es.thalesalv.chatrpg.infrastructure.inbound.api.controller.WorldController;
import es.thalesalv.chatrpg.infrastructure.security.authentication.DiscordPrincipal;
import es.thalesalv.chatrpg.infrastructure.security.authentication.DiscordUserDetailsService;
import es.thalesalv.chatrpg.infrastructure.security.authentication.config.AuthenticationSecurityConfig;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
@WebFluxTest(controllers = {
        AuthenticationController.class,
        WorldController.class
}, excludeAutoConfiguration = {
        ReactiveSecurityAutoConfiguration.class,
        AuthenticationSecurityConfig.class
})
public class AbstractRestControllerTest {

    @MockBean
    protected UseCaseRunner useCaseRunner;

    @MockBean
    protected ServerHttpSecurity serverHttpSecurity;

    @MockBean
    protected DiscordUserDetailsService discordUserDetailsService;

    @MockBean
    protected DiscordAuthenticationPort discordAuthenticationPort;

    @Mock
    protected Authentication authentication;

    @Autowired
    protected WebTestClient webTestClient;

    @BeforeEach
    public void before() throws Exception {

        webTestClient.head().header(HttpHeaders.AUTHORIZATION, "TOKEN");

        UserDetails userDetails = DiscordPrincipal.builder()
                .id("USRID")
                .email("user@email.com")
                .username("username")
                .build();

        when(discordUserDetailsService.findByUsername(anyString())).thenReturn(Mono.just(userDetails));
    }
}
