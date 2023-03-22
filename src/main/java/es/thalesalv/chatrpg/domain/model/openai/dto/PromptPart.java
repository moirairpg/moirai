package es.thalesalv.chatrpg.domain.model.openai.dto;

import es.thalesalv.chatrpg.domain.enums.ChatGptRole;
import es.thalesalv.chatrpg.domain.enums.Source;
import lombok.*;

@Builder
@Getter
public class PromptPart {
    private String content;
    private ChatGptRole role;
    private Source source;
    private int priority;
    private int insertionOrder;
    private int insertionPoint;
    private String prefix;
    private String suffix;
}
