package es.thalesalv.chatrpg.common.security.authentication;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

@Component
public class DiscordRequestFilter implements WebFilter {

    private final DiscordUserDetailsService userDetailsService;

    public DiscordRequestFilter(DiscordUserDetailsService userDetailsService) {

        this.userDetailsService = userDetailsService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String bearerToken = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
        if (StringUtils.isBlank(bearerToken)) {
            exchange.getResponse().setStatusCode(HttpStatusCode.valueOf(403));
            return exchange.getResponse().setComplete();
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            return userDetailsService.findByUsername(bearerToken).flatMap(userDetails -> {
                DiscordPrincipal user = (DiscordPrincipal) userDetails;
                UsernamePasswordAuthenticationToken authenticatedPrincipal =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

                return chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authenticatedPrincipal));
            });
        }

        return chain.filter(exchange);
    }
}
