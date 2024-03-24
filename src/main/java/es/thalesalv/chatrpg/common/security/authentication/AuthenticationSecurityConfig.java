package es.thalesalv.chatrpg.common.security.authentication;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class AuthenticationSecurityConfig {

    private final DiscordRequestFilter discordRequestFilter;

    @Bean
    SecurityWebFilterChain configure(ServerHttpSecurity http) {

        return http.addFilterBefore(discordRequestFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .authorizeExchange(exchanges -> exchanges.pathMatchers("/auth/**").permitAll())
                .authorizeExchange(exchanges -> exchanges.anyExchange().authenticated())
                .oauth2Login(withDefaults())
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .build();
    }
}
