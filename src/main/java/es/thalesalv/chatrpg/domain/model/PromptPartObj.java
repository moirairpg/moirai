package es.thalesalv.chatrpg.domain.model;

import es.thalesalv.chatrpg.domain.enums.ChatGptRole;
import es.thalesalv.chatrpg.domain.enums.Source;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.Optional;

@Builder
@Getter
public class PromptPartObj {
    private String content;
    private ChatGptRole role;
    private Source source;
    private int priority;
    private int insertionPoint;
    private Optional<OffsetDateTime> timeCreated;
    private String prefix;
    private String suffix;
}
