package es.thalesalv.chatrpg.infrastructure.inbound.api.errorhandler;

import java.util.Collections;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import es.thalesalv.chatrpg.common.exception.AuthenticationFailedException;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.ErrorResponse;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class WebExceptionHandler extends AbstractErrorWebExceptionHandler {

    private static final String UNKNOWN_ERROR = "An error has occurred. Please contact support.";

    public WebExceptionHandler(ErrorAttributes errorAttributes, WebProperties webProperties,
            ApplicationContext applicationContext, ServerCodecConfigurer configurer) {

        super(errorAttributes, webProperties.getResources(), applicationContext);
        this.setMessageWriters(configurer.getWriters());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {

        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {

        Throwable originalException = getError(request);

        if (originalException instanceof AuthenticationFailedException) {

            AuthenticationFailedException exception = (AuthenticationFailedException) originalException;
            ErrorResponse.Builder errorResponseBuilder = ErrorResponse.builder();
            errorResponseBuilder.code(HttpStatus.UNAUTHORIZED);

            if (StringUtils.isNotBlank(exception.getMessage())) {
                errorResponseBuilder.message(exception.getMessage());
            }

            if (StringUtils.isNotBlank(exception.getResponseMessage())) {
                errorResponseBuilder.details(Collections.singletonList(exception.getResponseMessage()));
            }

            return ServerResponse.status(401)
                    .bodyValue(errorResponseBuilder.build());
        }

        log.error("Unknown exception caught", originalException);
        return ServerResponse.status(500)
                .bodyValue(ErrorResponse.builder()
                        .code(HttpStatus.INTERNAL_SERVER_ERROR)
                        .message(UNKNOWN_ERROR)
                        .build());
    }
}
