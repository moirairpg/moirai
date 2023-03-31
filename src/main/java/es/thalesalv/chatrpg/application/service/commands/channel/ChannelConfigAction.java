package es.thalesalv.chatrpg.application.service.commands.channel;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ChannelConfigAction {

    GET("get"),
    SET("set"),
    LIST("list"),
    UNSET("unset");

    private final String actionName;

    ChannelConfigAction(final String actionName) {

        this.actionName = actionName;
    }

    public String getName() {

        return actionName;
    }

    public static Optional<ChannelConfigAction> byName(String name) {

        return Stream.of(values())
                .filter(a -> a.actionName.equals(name))
                .findAny();
    }

    public static String listAsString() {

        return Stream.of(values())
                .map(ChannelConfigAction::getName)
                .collect(Collectors.joining(", "));
    }
}
