package es.thalesalv.chatrpg.application.config;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Predicate;

@Value
@RequiredArgsConstructor
public class Nudge {
    public String role;
    public String content;
    public final static Predicate<Nudge> isValid = nudge -> !StringUtils.isEmpty(nudge.role) && ! StringUtils.isEmpty(nudge.content);
    @Override
    public String toString() {
        return content;
    }
}
