package es.thalesalv.chatrpg.domain.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.stream.Stream;

public enum Intent {

    @JsonProperty("rpg")
    RPG("rpg"),
    @JsonProperty("chat")
    CHAT("chat"),
    @JsonProperty("author")
    AUTHOR("author");

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
