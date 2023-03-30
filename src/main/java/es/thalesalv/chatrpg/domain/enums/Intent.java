package es.thalesalv.chatrpg.domain.enums;

import java.util.stream.Stream;

public enum Intent {

    RPG("rpg"), CHAT("chat"), AUTHOR("author");

    private final String text;

    Intent(String text) {

        this.text = text;
    }

    @Override
    public String toString() {

        return text;
    }

    public static Intent fromString(String text) {

        return Stream.of(values())
                .filter(i -> i.text.equals(text))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Unknown intent: " + text));
    }
}
