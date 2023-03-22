package es.thalesalv.chatrpg.domain.model.openai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.function.UnaryOperator;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PromptPart {
    private String content;
    private String role;
    private int priority;
    private int insertion;
    private String prefix;
    private String suffix;
    private UnaryOperator<String> formatter;
}
