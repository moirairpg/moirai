package es.thalesalv.chatrpg.core.domain;

import java.util.Arrays;

import es.thalesalv.chatrpg.common.exception.BusinessRuleViolationException;

public enum Visibility {

    PUBLIC,
    PRIVATE;

    public static Visibility fromString(String value) {

        return Arrays.stream(values())
                .filter(visibility -> visibility.name().equals(value.toUpperCase()))
                .findFirst()
                .orElseThrow(() -> new BusinessRuleViolationException("Invalid visibility"));
    }
}
