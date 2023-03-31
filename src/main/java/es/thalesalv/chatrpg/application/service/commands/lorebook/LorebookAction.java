package es.thalesalv.chatrpg.application.service.commands.lorebook;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum LorebookAction {

    GET("get"),
    LIST("list"),
    CREATE("create"),
    DELETE("delete"),
    EDIT("edit");

    private final String actionName;

    LorebookAction(final String actionName) {

        this.actionName = actionName;
    }

    public String getName() {

        return actionName;
    }

    public static Optional<LorebookAction> byName(String name) {

        return Stream.of(values())
                .filter(a -> a.actionName.equals(name))
                .findAny();
    }

    public static String listAsString() {

        return Stream.of(values())
                .map(LorebookAction::getName)
                .collect(Collectors.joining(", "));
    }
}
