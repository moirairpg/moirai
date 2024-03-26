package es.thalesalv.chatrpg.common.web;

import java.util.function.Function;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;

import es.thalesalv.chatrpg.infrastructure.security.authentication.DiscordPrincipal;
import reactor.core.publisher.Mono;

public abstract class SecurityContextAware {

    protected <T> Mono<T> withAuthenticatedUser(Function<DiscordPrincipal, T> function) {

        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication())
                .map(authentication -> (DiscordPrincipal) authentication.getPrincipal())
                .map(function);
    }

    protected <T> Mono<T> withAuthenticatedPrincipal(Function<Authentication, T> function) {

        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication())
                .map(function);
    }

    protected <T> Mono<T> withSecurityContext(Function<SecurityContext, T> function) {

        return ReactiveSecurityContextHolder.getContext().map(function);
    }
}
