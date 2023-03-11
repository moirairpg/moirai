package es.thalesalv.chatrpg.domain.exception;

import java.util.List;

import lombok.Getter;

@Getter
public class ModerationException extends RuntimeException {

    private final String flaggedContent;
    private final List<String> flaggedTopics;

    public ModerationException(String msg, List<String> flaggedTopics, String flaggedContent) {

        super(msg);
        this.flaggedContent = flaggedContent;
        this.flaggedTopics = flaggedTopics;
    }

    public ModerationException(String msg, String flaggedContent) {

        super(msg);
        this.flaggedContent = flaggedContent;
        this.flaggedTopics = null;
    }

    public ModerationException(String msg, List<String> flaggedTopics) {

        super(msg);
        this.flaggedContent = null;
        this.flaggedTopics = flaggedTopics;
    }

    public ModerationException(String msg) {

        super(msg);
        this.flaggedContent = null;
        this.flaggedTopics = null;
    }

    public ModerationException(Exception e) {

        super(e);
        this.flaggedContent = null;
        this.flaggedTopics = null;
    }
}
