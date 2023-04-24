package es.thalesalv.chatrpg.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Visibility {

    PUBLIC("public"),
    PRIVATE("private");

    private final String visibility;

    public static boolean isPublic(final String v) {

        return PUBLIC.getVisibility().equals(v);
    }
}
