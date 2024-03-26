package es.thalesalv.chatrpg.infrastructure.inbound.api.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import es.thalesalv.chatrpg.common.exception.AssetAccessDeniedException;
import es.thalesalv.chatrpg.common.exception.AssetNotFoundException;
import es.thalesalv.chatrpg.common.exception.AuthenticationFailedException;
import es.thalesalv.chatrpg.common.exception.BusinessRuleViolationException;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class WebExceptionHandler {

    private static final String UNKNOWN_ERROR = "An error has occurred. Please contact support.";
    private static final String ASSET_NOT_FOUND_ERROR = "The asset requested could not be found.";

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @ExceptionHandler(AssetNotFoundException.class)
    public ResponseEntity<ErrorResponse> assetNotFound(AssetNotFoundException exception) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(HttpStatus.NOT_FOUND)
                .message(ASSET_NOT_FOUND_ERROR)
                .details(Collections.singletonList(exception.getMessage()))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<ErrorResponse> assetNotFound(BusinessRuleViolationException exception) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(HttpStatus.UNPROCESSABLE_ENTITY)
                .details(Collections.singletonList(exception.getMessage()))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ErrorResponse> validationFailed(WebExchangeBindException exception) {

        List<String> errorMessages = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .toList();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(HttpStatus.BAD_REQUEST)
                .message(exception.getReason())
                .details(errorMessages)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(code = HttpStatus.FORBIDDEN)
    @ExceptionHandler(AssetAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> accessDeniedError(AssetAccessDeniedException exception) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(HttpStatus.FORBIDDEN)
                .message(exception.getMessage())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ErrorResponse> authenticationError(AuthenticationFailedException exception) {

        log.error("Error during authentication", exception);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(HttpStatus.UNAUTHORIZED)
                .message(exception.getMessage())
                .details(Collections.singletonList(exception.getResponseMessage()))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> unknownError(Exception exception) {

        log.error("An unknown error has occurred", exception);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(UNKNOWN_ERROR)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
