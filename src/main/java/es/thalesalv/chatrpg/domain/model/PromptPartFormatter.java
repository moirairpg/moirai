package es.thalesalv.chatrpg.domain.model;

import es.thalesalv.chatrpg.domain.enums.ChatGptRole;
import es.thalesalv.chatrpg.domain.enums.Source;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.EnumMap;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;

@NoArgsConstructor
public class PromptPartFormatter implements Function<PromptPartObj, String> {

    private final EnumMap<ChatGptRole, Function<String, String>> roleFormatters = new EnumMap<>(ChatGptRole.class);
    private final EnumMap<Source, Function<String, String>> sourceFormatters = new EnumMap<>(Source.class);

    public void addRoleFormatter(ChatGptRole role, UnaryOperator<String> formatter) {

        Function<String, String> fn = Optional.ofNullable(roleFormatters.get(role))
                .map(op -> op.andThen(formatter))
                .orElse(formatter);
        roleFormatters.put(role, fn);
    }

    public void addSourceFormatter(Source source, UnaryOperator<String> formatter) {

        Function<String, String> fn = Optional.ofNullable(sourceFormatters.get(source))
                .map(op -> op.andThen(formatter))
                .orElse(formatter);
        sourceFormatters.put(source, fn);
    }

    @Override
    public String apply(PromptPartObj part) {

        String content = part.getContent();
        if (StringUtils.isEmpty(content))
            return StringUtils.EMPTY;
        String prefix = Optional.ofNullable(part.getPrefix())
                .orElse(StringUtils.EMPTY);
        String suffix = Optional.ofNullable(part.getSuffix())
                .orElse(StringUtils.EMPTY);
        String toFormat = prefix + content + suffix;
        Function<String, String> roleFormatter = Optional.ofNullable(roleFormatters.get(part.getRole()))
                .orElse(Function.identity());
        Function<String, String> sourceFormatter = Optional.ofNullable(sourceFormatters.get(part.getSource()))
                .orElse(Function.identity());
        return roleFormatter.andThen(sourceFormatter)
                .apply(toFormat);
    }
}
