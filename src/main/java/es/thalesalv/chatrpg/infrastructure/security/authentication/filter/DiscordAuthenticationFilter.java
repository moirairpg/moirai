package es.thalesalv.chatrpg.infrastructure.security.authentication.filter;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import es.thalesalv.chatrpg.infrastructure.security.authentication.DiscordPrincipal;
import es.thalesalv.chatrpg.infrastructure.security.authentication.DiscordUserDetailsService;
import reactor.core.publisher.Mono;

@Component
public class DiscordAuthenticationFilter implements WebFilter {

    private static final int HTTP_FORBIDDEN = 403;

    private final List<String> ignoredPaths;
    private final DiscordUserDetailsService userDetailsService;

    public DiscordAuthenticationFilter(
            @Value("#{'${chatrpg.security.ignored-paths}'.split(',')}") List<String> ignoredPaths,
            DiscordUserDetailsService userDetailsService) {

        this.ignoredPaths = ignoredPaths;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public @NonNull Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {

        String requestPath = exchange.getRequest().getPath().value();
        if (!shouldPathBeIgnored(requestPath)) {

            List<String> authorizationHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
            if (authorizationHeader == null || authorizationHeader.isEmpty()) {
                exchange.getResponse().setStatusCode(HttpStatusCode.valueOf(HTTP_FORBIDDEN));
                return exchange.getResponse().setComplete();
            }

            String bearerToken = authorizationHeader.get(0);
            return userDetailsService.findByUsername(bearerToken).flatMap(userDetails -> {
                DiscordPrincipal user = (DiscordPrincipal) userDetails;
                UsernamePasswordAuthenticationToken authenticatedPrincipal = new UsernamePasswordAuthenticationToken(
                        user, null, user.getAuthorities());

                return chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authenticatedPrincipal));
            });
        }

        return chain.filter(exchange);
    }

    private boolean shouldPathBeIgnored(String path) {

        return ignoredPaths.stream().anyMatch(ignoredPath -> ignoredPath.contains(path));
    }
}
