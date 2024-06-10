package es.thalesalv.chatrpg.common.exception;

import java.util.List;

import lombok.Getter;

@Getter
public class ModerationException extends RuntimeException {

    private final List<String> flaggedTopics;

    public ModerationException(String message, List<String> flaggedTopics) {

        super(message);

        this.flaggedTopics = flaggedTopics;
    }
}
