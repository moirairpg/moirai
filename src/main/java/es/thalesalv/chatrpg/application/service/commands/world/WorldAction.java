package es.thalesalv.chatrpg.application.service.commands.world;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum WorldAction {

    GET("get"),
    LIST("list"),
    SET("set"),
    UNSET("unset");

    private final String actionName;

    WorldAction(final String actionName) {

        this.actionName = actionName;
    }

    public String getName() {

        return actionName;
    }

    public static Optional<WorldAction> byName(String name) {

        return Stream.of(values())
                .filter(a -> a.actionName.equals(name))
                .findAny();
    }

    public static String listAsString() {

        return Stream.of(values())
                .map(WorldAction::getName)
                .collect(Collectors.joining(", "));
    }
}
