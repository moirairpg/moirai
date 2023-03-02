package es.thalesalv.gptbot.domain.exception;

import java.util.List;

import lombok.Getter;

@Getter
public class ModerationException extends RuntimeException {

    private final List<String> flaggedTopics;

    public ModerationException(String msg, List<String> flaggedTopics) {

        super(msg);
        this.flaggedTopics = flaggedTopics;
    }

    public ModerationException(String msg) {

        super(msg);
        this.flaggedTopics = null;
    }
}
