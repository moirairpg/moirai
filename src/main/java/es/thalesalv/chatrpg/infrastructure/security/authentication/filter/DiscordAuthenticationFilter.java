package es.thalesalv.chatrpg.infrastructure.security.authentication.filter;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import es.thalesalv.chatrpg.infrastructure.security.authentication.DiscordPrincipal;
import es.thalesalv.chatrpg.infrastructure.security.authentication.DiscordUserDetailsService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class DiscordAuthenticationFilter implements WebFilter {

    private static final int HTTP_FORBIDDEN = 403;

    @Value("#{'${chatrpg.security.ignored-paths}'.split(',')}")
    private List<String> ignoredPaths;

    private final DiscordUserDetailsService userDetailsService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String requestPath = exchange.getRequest().getPath().value();
        if (!shouldPathBeIgnored(requestPath)) {

            List<String> authorizationHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
            if (CollectionUtils.isEmpty(authorizationHeader)) {
                exchange.getResponse().setStatusCode(HttpStatusCode.valueOf(HTTP_FORBIDDEN));
                return exchange.getResponse().setComplete();
            }

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                String bearerToken = authorizationHeader.get(0);
                return userDetailsService.findByUsername(bearerToken).flatMap(userDetails -> {
                    DiscordPrincipal user = (DiscordPrincipal) userDetails;
                    UsernamePasswordAuthenticationToken authenticatedPrincipal = new UsernamePasswordAuthenticationToken(
                            user, null, user.getAuthorities());

                    return chain.filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authenticatedPrincipal));
                });
            }
        }

        return chain.filter(exchange);
    }

    private boolean shouldPathBeIgnored(String path) {

        return ignoredPaths.stream().anyMatch(ignoredPath -> ignoredPath.contains(path));
    }
}
